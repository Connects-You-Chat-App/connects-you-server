package com.adarsh.connectsYouServer.models.requests

import com.adarsh.connectsYouServer.models.common.DeviceInfo

data class AuthRequest(
    val token: String,
    val fcmToken: String,
    val deviceInfo: DeviceInfo,
)