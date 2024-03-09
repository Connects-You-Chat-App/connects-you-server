package com.adarsh.connectsYouServer.models.requests

data class SaveUserKeysRequest(
    val publicKey: String,
    val privateKey: String,
)