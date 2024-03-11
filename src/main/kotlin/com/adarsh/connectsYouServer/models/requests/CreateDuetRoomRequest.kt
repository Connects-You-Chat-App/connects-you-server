package com.adarsh.connectsYouServer.models.requests

data class CreateDuetRoomRequest(
    val userId: String,
    val encryptedSharedKey: String,
)
