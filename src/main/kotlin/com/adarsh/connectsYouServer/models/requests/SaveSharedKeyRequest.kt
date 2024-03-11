package com.adarsh.connectsYouServer.models.requests

data class SaveSharedKeyRequest(
    val key: String,
    val forUserId: String? = null,
    val forRoomId: String? = null,
)
