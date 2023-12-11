package com.adarsh.connects_you_server.models.common

data class UserJWTClaim(
    var id: String,
    var name: String,
    var email: String,
    var deviceId: String,
) {
    constructor() : this("", "", "", "")
}