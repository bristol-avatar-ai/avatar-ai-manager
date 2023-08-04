package com.example.ai_avatar_manager.network

import com.squareup.moshi.Json

/**
 * JSON response data from an IAM Token Request is mapped to the following data class.
 *
 * Example response:
 * {
 *     "access_token": "{access_token}",
 *     "refresh_token": "not_supported",
 *     "token_type": "Bearer",
 *     "expires_in": 3600,
 *     "expiration": 1690819387,
 *     "scope": "ibm openid"
 * }
 */

data class IamTokenResult (
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "expires_in") val expiresIn: Int,
    val expiration: Int,
    val scope: String
)