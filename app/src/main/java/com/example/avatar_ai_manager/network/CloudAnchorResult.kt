package com.example.avatar_ai_manager.network

/**
 * JSON response data for cloud anchor information is mapped to the following data classes.
 *
 * Example response:
 * {
 *     "anchors": [
 *         {
 *             "name": "anchors/ua-a1cc84e4f11b1287d289646811bf54d1",
 *             "createTime": "...",
 *             "expireTime": "...",
 *             "lastLocalizeTime": "...",
 *             "maximumExpireTime": "..."
 *         },
 *         {
 *             "name": "anchors/ua-41a3d0233471917875159f6f3c25ea0e",
 *             "createTime": "...",
 *             "expireTime": "...",
 *             "lastLocalizeTime": "...",
 *             "maximumExpireTime": "..."
 *         }
 *     ],
 *     "nextPageToken": "some-long-string"
 * }
 */

data class CloudAnchorResult(
    val anchors: List<CloudAnchor>?,
    val nextPageToken: String?
)

data class CloudAnchor(
    val name: String,
    val createTime: String,
    val expireTime: String,
    val lastLocalizeTime: String,
    val maximumExpireTime: String
)