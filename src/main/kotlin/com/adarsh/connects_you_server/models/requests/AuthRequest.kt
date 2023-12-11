package com.adarsh.connects_you_server.models.requests

import com.adarsh.connects_you_server.models.common.DeviceInfo

data class AuthRequest(
    val token: String,
    val fcmToken: String,
    val deviceInfo: DeviceInfo
)