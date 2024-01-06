package com.adarsh.connects_you_server.models.responses

import com.adarsh.connects_you_server.models.entities.UserSharedKey

data class GetUserSharedKeysResponse(
    val keys: List<UserSharedKey>
)