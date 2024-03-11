package com.adarsh.connectsYouServer.models.responses

import com.adarsh.connectsYouServer.models.entities.UserSharedKey
import java.util.Date

data class UserSharedKeyResponse(
    val key: String,
    val forRoomId: String?,
    val forUserId: String?,
    val createdAt: Date,
    val updatedAt: Date,
)

data class GetUserSharedKeysResponse(
    val keys: List<UserSharedKeyResponse>,
) {
    companion object {
        fun fromListOfUserSharedKey(userSharedKeys: List<UserSharedKey>): GetUserSharedKeysResponse {
            return GetUserSharedKeysResponse(
                userSharedKeys.map {
                    UserSharedKeyResponse(
                        key = it.key,
                        forRoomId = it.forRoom?.id?.toString() ?: null,
                        forUserId = it.forUser?.id?.toString() ?: null,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                    )
                },
            )
        }
    }
}
