package com.adarsh.connectsYouServer.models.responses

import com.adarsh.connectsYouServer.models.entities.User

data class AuthResponse(
    val token: String,
    val method: String,
    val user: User? = null,
)