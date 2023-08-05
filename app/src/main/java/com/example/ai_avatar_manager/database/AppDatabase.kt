package com.example.ai_avatar_manager.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ai_avatar_manager.network.CloudStorageApi.updateDatabase
import java.io.File

private const val TAG = "AppDatabase"

@Database(entities = [Anchor::class, Path::class, Exhibition::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun anchorDao(): AnchorDao
    abstract fun pathDao(): PathDao
    abstract fun exhibitionDao(): ExhibitionDao

    companion object {
        const val FILENAME = "data"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        suspend fun getDatabase(context: Context): AppDatabase? {
            val databaseFile = File(context.filesDir, FILENAME)
            return INSTANCE ?: if (
                updateDatabase(databaseFile) || databaseFile.exists()
            ) {
                createDatabase(context, databaseFile)
            } else {
                Log.e(TAG, "Unable to download database")
                null
            }
        }

        private fun createDatabase(context: Context, databaseFile: File): AppDatabase {
            return synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    databaseFile.toPath().toString()
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }

        fun close() {
            INSTANCE?.close()
            INSTANCE = null
        }

    }
}