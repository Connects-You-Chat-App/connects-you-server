package com.adarsh.connectsYouServer.models.requests

data class EditMessageRequest(
    val messageId: String,
    val message: String,
)
