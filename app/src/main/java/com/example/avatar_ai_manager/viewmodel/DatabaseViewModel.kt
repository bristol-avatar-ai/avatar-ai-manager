package com.example.avatar_ai_manager.viewmodel

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avatar_ai_cloud_storage.database.AppDatabase
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_cloud_storage.database.entity.Feature
import com.example.avatar_ai_cloud_storage.database.entity.Path
import com.example.avatar_ai_cloud_storage.network.CloudStorageApi
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "DatabaseViewModel"

private const val SNACK_BAR_DURATION = 2000

class DatabaseViewModel : ViewModel() {

    enum class Status { LOADING, READY, ERROR }

    private val _status = MutableLiveData(Status.ERROR)
    val status: LiveData<Status> get() = _status

    private var _database: AppDatabase? = null
    private val database get() = _database!!
    private val anchorDao get() = database.anchorDao()
    private val featureDao get() = database.featureDao()
    private val pathDao get() = database.pathDao()

    private fun updateStatus() {
        _status.postValue(
            if (_database == null) {
                Status.ERROR
            } else {
                Status.READY
            }
        )
    }

    fun init(context: Context) {
        if (_database == null) {
            _status.postValue(Status.LOADING)
            viewModelScope.launch(Dispatchers.IO) {
                _database = AppDatabase.getDatabase(context)
                updateStatus()
            }
        }
    }

    fun reload(context: Context) {
        AppDatabase.close()
        _database = null
        File(context.filesDir, AppDatabase.FILENAME).delete()
        _status.postValue(Status.LOADING)
        init(context)
    }

    suspend fun uploadDatabase(context: Context): Boolean {
        _status.postValue(Status.LOADING)
        val databaseFile = File(context.filesDir, AppDatabase.FILENAME)
        AppDatabase.close()
        _database = null
        var success = false
        if (databaseFile.exists()) {
            success = CloudStorageApi.uploadDatabase(databaseFile)
        }
        _database = AppDatabase.getDatabase(context)
        updateStatus()
        return success
    }

    fun showMessage(activity: Activity, message: String) {
        val view = activity.findViewById<View>(android.R.id.content)
        Snackbar.make(view, message, SNACK_BAR_DURATION).show()
    }

    fun getAnchors(): Flow<List<Anchor>> {
        return anchorDao.getAnchorsFlow()
    }

    fun getFeaturesAtAnchor(anchorId: String): Flow<List<Feature>> {
        return featureDao.getFeaturesAtAnchor(anchorId)
    }

    fun getPathsFromAnchor(anchorId: String): Flow<List<Path>> {
        return pathDao.getPathsFromAnchor(anchorId)
    }

    suspend fun addAnchor(anchor: Anchor) {
        anchorDao.insert(anchor)
    }

    suspend fun updateAnchor(anchorId: String, description: String) {
        anchorDao.update(anchorId, description)
    }

    suspend fun deleteAnchor(anchorId: String) {
        anchorDao.delete(anchorId)
    }

    suspend fun addFeature(feature: Feature) {
        featureDao.insert(feature)
    }

    suspend fun updateFeature(name: String, description: String) {
        featureDao.update(name, description)
    }

    suspend fun deleteFeature(name: String) {
        featureDao.delete(name)
    }

    /*
    * Adds a path to the database. A SQLiteConstraintException is thrown if
    * the anchors are the same, or if the combination already exists. Paths
    * are bidirectional, so the anchor1 and anchor2 are always sorted to
    * prevent path duplication.
     */
    suspend fun addPath(anchor1: String, anchor2: String, distance: Int) {
        if (anchor1 == anchor2) {
            throw SQLiteConstraintException(
                "UNIQUE constraint failed: anchor1 must not be identical to anchor2"
            )
        }
        val sortedAnchors = listOf(anchor1, anchor2).sorted()
        pathDao.insert(Path(sortedAnchors[0], sortedAnchors[1], distance))
    }

    suspend fun updatePath(anchor1: String, anchor2: String, distance: Int) {
        val sortedAnchors = listOf(anchor1, anchor2).sorted()
        pathDao.update(sortedAnchors[0], sortedAnchors[1], distance)
    }

    suspend fun deletePath(anchor1: String, anchor2: String) {
        val sortedAnchors = listOf(anchor1, anchor2).sorted()
        pathDao.delete(sortedAnchors[0], sortedAnchors[1])
    }

}