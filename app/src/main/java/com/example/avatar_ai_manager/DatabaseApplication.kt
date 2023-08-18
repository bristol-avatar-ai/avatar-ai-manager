package com.example.avatar_ai_manager

import android.app.Application
import com.example.avatar_ai_cloud_storage.database.AppDatabase

class DatabaseApplication : Application() {
    var database: AppDatabase? = null
}