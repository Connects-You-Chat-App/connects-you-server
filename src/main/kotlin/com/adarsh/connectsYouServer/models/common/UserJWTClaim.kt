package com.adarsh.connectsYouServer.models.common

data class UserJWTClaim(
    var id: String,
    var name: String,
    var email: String,
    var deviceId: String,
) {
    constructor() : this("", "", "", "")
}
