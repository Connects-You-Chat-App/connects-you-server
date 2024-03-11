package com.adarsh.connectsYouServer.models.responses

import com.adarsh.connectsYouServer.models.entities.User

data class UsersResponse(
    val users: List<User>,
)
