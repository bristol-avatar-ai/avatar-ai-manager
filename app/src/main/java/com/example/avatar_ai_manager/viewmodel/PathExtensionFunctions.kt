package com.example.avatar_ai_manager.viewmodel

import com.example.avatar_ai_cloud_storage.database.entity.Path

private const val TAG = "PathExtensionFunctions"

fun Path.getDestinationId(originId: String): String {
    return if (anchor1 == originId) {
        anchor2
    } else {
        anchor1
    }
}

