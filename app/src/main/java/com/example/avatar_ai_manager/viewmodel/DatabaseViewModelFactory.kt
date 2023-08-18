package com.example.avatar_ai_manager.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * A ViewModelFactory for creating instances of [DatabaseViewModel].
 *
 * @param application The [Application] instance used to initialize the ViewModel.
 */
class DatabaseViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    /**
     * Creates and returns an instance of [DatabaseViewModel].
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return An instance of the specified ViewModel class.
     * @throws IllegalArgumentException if [modelClass] is not assignable from [DatabaseViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatabaseViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}