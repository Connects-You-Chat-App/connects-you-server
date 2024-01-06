package com.adarsh.connects_you_server.models.requests


data class JoinGroupRequest(
    val invitationId: String,
    val selfEncryptedRoomSecretKey: String,
)