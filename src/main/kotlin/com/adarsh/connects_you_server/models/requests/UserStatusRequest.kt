package com.adarsh.connects_you_server.models.requests

data class UserStatusRequest(
    val status: String,
    val validTill: Long
)