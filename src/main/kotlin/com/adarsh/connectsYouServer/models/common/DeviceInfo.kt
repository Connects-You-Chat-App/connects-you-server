package com.adarsh.connectsYouServer.models.common

data class DeviceInfo(
    var deviceId: String,
    var deviceName: String,
    var deviceOS: String,
) {
    constructor() : this("", "", "")
}
