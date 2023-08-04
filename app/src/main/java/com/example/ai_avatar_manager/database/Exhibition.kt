package com.example.ai_avatar_manager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exhibition",
    foreignKeys = [
        ForeignKey(
            entity = Anchor::class,
            parentColumns = ["id"],
            childColumns = ["anchor"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Exhibition(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "anchor")
    val anchor: String,
    @ColumnInfo(name = "description")
    val description: String
)