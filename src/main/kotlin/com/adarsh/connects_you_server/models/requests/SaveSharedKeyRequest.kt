package com.adarsh.connects_you_server.models.requests

data class SaveSharedKeyRequest(
    val key: String,
    val forUserId: String?,
    val forRoomId: String?,
)