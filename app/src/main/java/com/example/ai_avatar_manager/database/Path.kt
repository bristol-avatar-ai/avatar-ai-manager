package com.example.ai_avatar_manager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "path",
    primaryKeys = ["origin", "destination"],
    foreignKeys = [
        ForeignKey(
            entity = Anchor::class,
            parentColumns = ["id"],
            childColumns = ["origin"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Anchor::class,
            parentColumns = ["id"],
            childColumns = ["destination"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Path(
    @ColumnInfo(name = "origin")
    val origin: String,
    @ColumnInfo(name = "destination")
    val destination: String,
    @ColumnInfo(name = "distance")
    val distance: Int
)