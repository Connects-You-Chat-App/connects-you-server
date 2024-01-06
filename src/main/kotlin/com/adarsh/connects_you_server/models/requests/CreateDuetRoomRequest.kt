package com.adarsh.connects_you_server.models.requests

data class CreateDuetRoomRequest(
    val userId: String,
    val encryptedSharedKey: String,
)