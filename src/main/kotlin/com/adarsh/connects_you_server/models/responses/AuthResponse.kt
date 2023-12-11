package com.adarsh.connects_you_server.models.responses

import com.adarsh.connects_you_server.models.entities.User

data class AuthResponse(
    val token: String,
    val method: String,
    val user: User,
)