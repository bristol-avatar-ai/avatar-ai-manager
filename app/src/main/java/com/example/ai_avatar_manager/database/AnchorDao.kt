package com.example.ai_avatar_manager.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnchorDao {
    @Query("SELECT * FROM anchor")
    fun getAnchors(): Flow<List<Anchor>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(anchor: Anchor)

    @Query("UPDATE anchor SET description = :description WHERE id LIKE :anchorId")
    suspend fun update(anchorId: String, description: String)

    @Query("DELETE FROM anchor WHERE id LIKE :anchorId")
    suspend fun delete(anchorId: String)
}