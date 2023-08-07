package com.example.ai_avatar_manager.viewmodel

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avatar_ai_cloud_storage.database.AppDatabase
import com.example.avatar_ai_cloud_storage.network.CloudStorageApi
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "DatabaseViewModel"

private const val SNACK_BAR_DURATION = 2000

class DatabaseViewModel : ViewModel() {

    private val _isReady = MutableLiveData<Boolean?>(null)
    val isReady: LiveData<Boolean?>
        get() = _isReady
    private var _database: AppDatabase? = null
    private val database get() = _database!!
    private val anchorDao get() = database.anchorDao()
    private val exhibitionDao get() = database.exhibitionDao()
    private val pathDao get() = database.pathDao()

    private fun updateIsReady() {
        _isReady.postValue(_database != null)
    }

    fun init(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _database = AppDatabase.getDatabase(context)
            updateIsReady()
        }
    }

    fun close(context: Context) {
        AppDatabase.close()
        _database = null
        File(context.filesDir, AppDatabase.FILENAME).delete()
        _isReady.value = null
    }

    suspend fun uploadDatabase(context: Context): Boolean {
        val databaseFile = File(context.filesDir, AppDatabase.FILENAME)
        _database?.close()
        _database = AppDatabase.getDatabase(context)
        updateIsReady()
        return if (databaseFile.exists()) {
            CloudStorageApi.uploadDatabase(databaseFile)
        } else {
            false
        }
    }

    fun showMessage(activity: Activity, message: String) {
        val view = activity.findViewById<View>(android.R.id.content)
        Snackbar.make(view, message, SNACK_BAR_DURATION).show()
    }

    fun getAnchors(): Flow<List<com.example.avatar_ai_cloud_storage.database.Anchor>> {
        return anchorDao.getAnchors()
    }

    fun getExhibitionsAtAnchor(anchorId: String): Flow<List<com.example.avatar_ai_cloud_storage.database.Exhibition>> {
        return exhibitionDao.getExhibitionsAtAnchor(anchorId)
    }

    fun getPathsFromAnchor(anchorId: String): Flow<List<com.example.avatar_ai_cloud_storage.database.Path>> {
        return pathDao.getPathsFromAnchor(anchorId)
    }

    suspend fun updateAnchor(anchorId: String, description: String) {
        anchorDao.update(anchorId, description)
    }

    suspend fun addAnchor(anchor: com.example.avatar_ai_cloud_storage.database.Anchor) {
        anchorDao.insert(anchor)
    }

    suspend fun deleteAnchor(anchorId: String) {
        anchorDao.delete(anchorId)
    }

    suspend fun updateExhibition(name: String, description: String) {
        exhibitionDao.update(name, description)
    }

    suspend fun addExhibition(exhibition: com.example.avatar_ai_cloud_storage.database.Exhibition) {
        exhibitionDao.insert(exhibition)
    }

    suspend fun deleteExhibition(name: String) {
        exhibitionDao.delete(name)
    }

    suspend fun updatePath(origin: String, destination: String, distance: Int) {
        pathDao.update(origin, destination, distance)
    }

    suspend fun addPath(path: com.example.avatar_ai_cloud_storage.database.Path) {
        pathDao.insert(path)
    }

    suspend fun deletePath(origin: String, destination: String) {
        pathDao.delete(origin, destination)
    }


}