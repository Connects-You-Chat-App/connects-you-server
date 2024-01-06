package com.adarsh.connects_you_server.models.responses

import com.adarsh.connects_you_server.models.entities.User

data class UsersResponse(
    val users: List<User>
)