package com.adarsh.connectsYouServer.models.requests

data class MarkMessageAsReadRequest(
    val messageIds: List<String>,
    val roomId: String,
)