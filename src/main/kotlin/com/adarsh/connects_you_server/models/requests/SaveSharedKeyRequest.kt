package com.adarsh.connects_you_server.models.requests

data class SaveSharedKeyRequest(
    val key: String,
    val forUserId: String? = null,
    val forRoomId: String? = null,
)