package com.adarsh.connectsYouServer.models.requests

data class UserStatusRequest(
    val status: String,
    val validTill: Long,
)