package com.example.ai_avatar_manager.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PathDao {
    @Query("SELECT * FROM path")
    fun getPaths(): Flow<List<Path>>

    @Query("SELECT * FROM path WHERE origin LIKE :anchorId")
    fun getPathsFromAnchor(anchorId: String): Flow<List<Path>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(path: Path)

    @Update
    suspend fun update(path: Path)

    @Delete
    suspend fun delete(path: Path)
}