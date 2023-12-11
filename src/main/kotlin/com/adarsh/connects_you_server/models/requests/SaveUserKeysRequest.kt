package com.adarsh.connects_you_server.models.requests

data class SaveUserKeysRequest(
    val publicKey: String,
    val privateKey: String
)