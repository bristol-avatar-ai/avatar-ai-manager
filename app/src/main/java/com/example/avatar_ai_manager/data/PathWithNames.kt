package com.example.avatar_ai_manager.data

private const val TAG = "PathWithNames"

data class PathWithNames(
    val anchor1Id: String,
    val anchor1Name: String,
    val anchor2Id: String,
    val anchor2Name: String,
    val distance: Int
) {

    fun getDestinationId(originId: String): String {
        return if (anchor1Id == originId) {
            anchor2Id
        } else {
            anchor1Id
        }
    }

    fun getDestinationName(originId: String): String {
        return if (anchor1Id == originId) {
            anchor2Name
        } else {
            anchor1Name
        }
    }

}