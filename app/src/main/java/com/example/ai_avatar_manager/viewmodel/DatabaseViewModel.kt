package com.example.ai_avatar_manager.viewmodel

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_avatar_manager.database.Anchor
import com.example.ai_avatar_manager.database.AppDatabase
import com.example.ai_avatar_manager.database.Exhibition
import com.example.ai_avatar_manager.database.Path
import com.example.ai_avatar_manager.network.CloudStorageApi
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "DatabaseViewModel"

private const val SNACK_BAR_DURATION = 2000

interface DatabaseViewModelCallBack {
    companion object {
        const val SUCCESS = 0
        const val FAILURE = 1
    }

    fun onDatabaseViewModelInit(status: Int)
}

class DatabaseViewModel : ViewModel() {

    private var _database: AppDatabase? = null
    private val database get() = _database!!
    private val anchorDao get() = database.anchorDao()
    private val exhibitionDao get() = database.exhibitionDao()
    private val pathDao get() = database.pathDao()

    fun init(context: Context, databaseViewModelCallBack: DatabaseViewModelCallBack) {
        viewModelScope.launch(Dispatchers.IO) {
            _database = AppDatabase.getDatabase(context)
            databaseViewModelCallBack.onDatabaseViewModelInit(
                when (_database != null) {
                    true -> DatabaseViewModelCallBack.SUCCESS
                    false -> DatabaseViewModelCallBack.FAILURE
                }
            )
        }
    }

    fun isReady(): Boolean {
        return _database != null
    }

    fun close(context: Context) {
        AppDatabase.close()
        _database = null
        File(context.filesDir, AppDatabase.FILENAME).delete()
    }

    suspend fun uploadDatabase(context: Context): Boolean {
        val databaseFile = File(context.filesDir, AppDatabase.FILENAME)
        _database?.close()
        val isSuccess = CloudStorageApi.uploadDatabase(databaseFile)
        _database = AppDatabase.getDatabase(context)
        return isSuccess
    }

    fun showMessage(activity: Activity, message: String) {
        val view = activity.findViewById<View>(android.R.id.content)
        Snackbar.make(view, message, SNACK_BAR_DURATION).show()
    }

    fun getAnchors(): Flow<List<Anchor>> {
        return anchorDao.getAnchors()
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