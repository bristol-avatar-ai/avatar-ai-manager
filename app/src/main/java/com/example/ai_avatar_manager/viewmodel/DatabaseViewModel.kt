package com.example.ai_avatar_manager.viewmodel

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avatar_ai_cloud_storage.database.Anchor
import com.example.avatar_ai_cloud_storage.database.AppDatabase
import com.example.avatar_ai_cloud_storage.database.Exhibition
import com.example.avatar_ai_cloud_storage.database.Path
import com.example.avatar_ai_cloud_storage.network.CloudStorageApi
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "DatabaseViewModel"

private const val SNACK_BAR_DURATION = 2000

class DatabaseViewModel : ViewModel() {

    enum class Status { LOADING, READY, NULL }

    private val _status = MutableLiveData(Status.NULL)
    val status: LiveData<Status> get() = _status

    private var _database: AppDatabase? = null
    private val database get() = _database!!
    private val anchorDao get() = database.anchorDao()
    private val exhibitionDao get() = database.exhibitionDao()
    private val pathDao get() = database.pathDao()

    private fun updateStatus() {
        _status.postValue(
            if (_database == null) {
                Status.NULL
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

    fun getExhibitionsAtAnchor(anchorId: String): Flow<List<Exhibition>> {
        return exhibitionDao.getExhibitionsAtAnchor(anchorId)
    }

    fun getPathsFromAnchor(anchorId: String): Flow<List<Path>> {
        return pathDao.getPathsFromAnchor(anchorId)
    }

    suspend fun updateAnchor(anchorId: String, description: String) {
        anchorDao.update(anchorId, description)
    }

    suspend fun addAnchor(anchor: Anchor) {
        anchorDao.insert(anchor)
    }

    suspend fun deleteAnchor(anchorId: String) {
        anchorDao.delete(anchorId)
    }

    suspend fun updateExhibition(name: String, description: String) {
        exhibitionDao.update(name, description)
    }

    suspend fun addExhibition(exhibition: Exhibition) {
        exhibitionDao.insert(exhibition)
    }

    suspend fun deleteExhibition(name: String) {
        exhibitionDao.delete(name)
    }

    suspend fun updatePath(origin: String, destination: String, distance: Int) {
        pathDao.update(origin, destination, distance)
    }

    suspend fun addPath(path: Path) {
        pathDao.insert(path)
    }

    suspend fun deletePath(origin: String, destination: String) {
        pathDao.delete(origin, destination)
    }

}