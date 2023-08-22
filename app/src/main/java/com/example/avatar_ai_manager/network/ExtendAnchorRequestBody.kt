package com.example.avatar_ai_manager.network

import com.squareup.moshi.Json

data class ExtendAnchorRequestBody(
    @Json(name = "expireTime") val expireTime: String
)