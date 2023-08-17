package com.example.avatar_ai_manager.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.avatar_ai_cloud_storage.database.AppDatabase
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_cloud_storage.database.entity.Feature
import com.example.avatar_ai_cloud_storage.database.entity.Path
import com.example.avatar_ai_cloud_storage.network.CloudStorageApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "DatabaseViewModel"

class DatabaseViewModel(application: Application) : AndroidViewModel(application) {

    enum class Status { LOADING, READY, ERROR }

    private val context get() = getApplication<Application>().applicationContext

    private val databaseFile = File(context.filesDir, AppDatabase.FILENAME)

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status> get() = _status

    private var database: AppDatabase? = null
    private val anchorDao get() = database?.anchorDao()
    private val featureDao get() = database?.featureDao()
    private val pathDao get() = database?.pathDao()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadDatabase()
        }
    }

    private suspend fun loadDatabase() {
        if (database == null) {
            _status.postValue(Status.LOADING)
            database =
                AppDatabase.getDatabase(context)
            updateStatus()
        }
    }

    private fun updateStatus() {
        _status.postValue(
            if (database == null) {
                Status.ERROR
            } else {
                Status.READY
            }
        )
    }

    suspend fun reload() {
        AppDatabase.close()
        database = null
        databaseFile.delete()
        loadDatabase()
    }

    suspend fun uploadDatabase(): Boolean {
        _status.postValue(Status.LOADING)
        AppDatabase.close()
        database = null

        var success = false
        if (databaseFile.exists()) {
            success = CloudStorageApi.uploadDatabase(databaseFile)
        }
        database = AppDatabase.getDatabase(context)
        updateStatus()
        return success
    }

    fun getFeatures(): Flow<List<Feature>>? {
        return featureDao?.getFeaturesFlow()
    }

    suspend fun addFeature(feature: Feature) {
        featureDao?.insert(feature)
    }

    suspend fun updateFeature(name: String, description: String) {
        featureDao?.update(name, description)
    }

    suspend fun deleteFeature(name: String) {
        featureDao?.delete(name)
    }

    fun getAnchors(): Flow<List<Anchor>>? {
        return anchorDao?.getAnchorsFlow()
    }

    suspend fun addAnchor(anchor: Anchor) {
        anchorDao?.insert(anchor)
    }

    suspend fun updateAnchor(anchorId: String, description: String) {
        anchorDao?.update(anchorId, description)
    }

    suspend fun deleteAnchor(anchorId: String) {
        anchorDao?.delete(anchorId)
    }

    fun getAnchorsWithPathCounts(): Flow<List<AnchorWithPathCount>>? {
        return getAnchors()?.map { anchorList ->
            anchorList.map { anchor ->
                AnchorWithPathCount(
                    anchor.id,
                    anchor.description,
                    pathDao?.countPathsFromAnchor(anchor.id) ?: 0
                )
            }
        }
    }

    fun getPathsFromAnchor(anchorId: String): Flow<List<Path>>? {
        return pathDao?.getPathsFromAnchor(anchorId)
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
        pathDao?.insert(Path(sortedAnchors[0], sortedAnchors[1], distance))
    }

    suspend fun updatePath(anchor1: String, anchor2: String, distance: Int) {
        val sortedAnchors = listOf(anchor1, anchor2).sorted()
        pathDao?.update(sortedAnchors[0], sortedAnchors[1], distance)
    }

    suspend fun deletePath(anchor1: String, anchor2: String) {
        val sortedAnchors = listOf(anchor1, anchor2).sorted()
        pathDao?.delete(sortedAnchors[0], sortedAnchors[1])
    }

}