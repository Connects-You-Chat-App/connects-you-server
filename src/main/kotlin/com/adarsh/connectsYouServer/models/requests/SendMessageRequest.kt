package com.adarsh.connectsYouServer.models.requests

data class SendMessageRequest(
    val roomId: String,
    val messageId: String,
    val message: String,
    val type: String,
    val belongsToMessageId: String? = null,
    val forwardedFromRoomId: String? = null,
)
