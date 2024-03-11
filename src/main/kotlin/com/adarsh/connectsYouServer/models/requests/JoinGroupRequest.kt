package com.adarsh.connectsYouServer.models.requests

data class JoinGroupRequest(
    val invitationId: String,
    val selfEncryptedRoomSecretKey: String,
)
