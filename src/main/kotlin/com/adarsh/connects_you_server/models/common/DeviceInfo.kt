package com.adarsh.connects_you_server.models.common

data class DeviceInfo(
    var deviceId: String,
    var deviceName: String,
    var deviceOS: String
) {
    constructor() : this("", "", "")
}