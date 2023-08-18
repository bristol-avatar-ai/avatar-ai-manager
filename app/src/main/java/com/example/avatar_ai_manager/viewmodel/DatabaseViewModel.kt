package com.example.avatar_ai_manager.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.avatar_ai_cloud_storage.database.AppDatabase
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_cloud_storage.database.entity.Feature
import com.example.avatar_ai_cloud_storage.database.entity.Path
import com.example.avatar_ai_cloud_storage.database.entity.PrimaryFeature
import com.example.avatar_ai_cloud_storage.network.CloudStorageApi
import com.example.avatar_ai_manager.DatabaseApplication
import com.example.avatar_ai_manager.data.AnchorWithPathCount
import com.example.avatar_ai_manager.data.PathWithNames
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

    private var database
        get() = getApplication<DatabaseApplication>().database
        set(value) {
            getApplication<DatabaseApplication>().database = value
        }
    private val featureDao get() = database?.featureDao()
    private val primaryFeatureDao get() = database?.primaryFeatureDao()
    private val anchorDao get() = database?.anchorDao()
    private val pathDao get() = database?.pathDao()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            load()
        }
    }

    private fun postStatus(status: Status) {
        if (status == Status.ERROR) {
            Log.w(TAG, "Status: $status")
        } else {
            Log.i(TAG, "Status: $status")
        }
        _status.postValue(status)
    }

    private suspend fun load() {
        if (database == null) {
            postStatus(Status.LOADING)
            database = AppDatabase.getDatabase(context)
        }
        updateStatus()
    }

    private fun updateStatus() {
        postStatus(
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
        load()
    }

    suspend fun upload(): Boolean {
        postStatus(Status.LOADING)
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

    suspend fun isPrimaryFeature(name: String, anchorId: String): Boolean {
        return primaryFeatureDao?.getPrimaryFeature(anchorId)?.feature == name
    }

    suspend fun addPrimaryFeature(anchorId: String, featureName: String) {
        primaryFeatureDao?.insert(PrimaryFeature(anchorId, featureName))
    }

    suspend fun deletePrimaryFeature(featureName: String) {
        primaryFeatureDao?.delete(featureName)
    }

    fun getAnchors(): Flow<List<Anchor>>? {
        return anchorDao?.getAnchorsFlow()
    }

    suspend fun getAnchor(anchorId: String): Anchor? {
        return anchorDao?.getAnchor(anchorId)
    }

    suspend fun addAnchor(anchor: Anchor) {
        anchorDao?.insert(anchor)
    }

    suspend fun updateAnchor(anchorId: String, name: String) {
        anchorDao?.update(anchorId, name)
    }

    suspend fun deleteAnchor(anchorId: String) {
        anchorDao?.delete(anchorId)
    }

    fun getAnchorsWithPathCounts(): Flow<List<AnchorWithPathCount>>? {
        return getAnchors()?.map { anchorList ->
            anchorList.map { anchor ->
                AnchorWithPathCount(
                    anchor.id,
                    anchor.name,
                    pathDao?.countPathsFromAnchor(anchor.id) ?: 0
                )
            }
        }
    }

    fun getPathsWithNamesFromAnchor(anchorId: String): Flow<List<PathWithNames>>? {
        return pathDao?.getPathsFromAnchor(anchorId)?.map { pathList ->
            val originName = anchorDao?.getAnchor(anchorId)?.name.toString()
            pathList.map { path ->
                PathWithNames(
                    path.anchor1,
                    getAnchorName(path.anchor1, anchorId, originName),
                    path.anchor2,
                    getAnchorName(path.anchor2, anchorId, originName),
                    path.distance
                )
            }
        }
    }

    private suspend fun getAnchorName(
        anchorId: String,
        originId: String,
        originName: String
    ): String {
        return if (anchorId == originId) {
            originName
        } else {
            anchorDao?.getAnchor(anchorId)?.name.toString()
        }
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