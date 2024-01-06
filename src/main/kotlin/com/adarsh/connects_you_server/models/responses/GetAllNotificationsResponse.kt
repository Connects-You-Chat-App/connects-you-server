package com.adarsh.connects_you_server.models.responses

import com.adarsh.connects_you_server.models.entities.GroupInvitation
import com.adarsh.connects_you_server.models.entities.User
import com.adarsh.connects_you_server.utils.JSON
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class NotificationResponse(
    val id: String,
    val roomId: String,
    val senderUser: User,
    val receiverUserId: String,
    val message: String,
    val encryptedRoomSecretKey: String,
    @get:JsonProperty("isAccepted")
    val isAccepted: Boolean = false,
    val sendAt: Date,
) {
    constructor() : this("", "", User(), "", "", "", false, Date())

    companion object {
        fun fromGroupNotification(groupInvitation: GroupInvitation): NotificationResponse {
            return NotificationResponse(
                groupInvitation.id.toString(),
                groupInvitation.room.id.toString(),
                groupInvitation.senderUser,
                groupInvitation.receiverUser.id.toString(),
                groupInvitation.message,
                groupInvitation.encryptedRoomSecretKey,
                groupInvitation.isAccepted,
                groupInvitation.sendAt
            )

        }

        fun fromJson(json: String): NotificationResponse {
            return JSON.fromJson(json, NotificationResponse::class.java)
        }
    }

    fun toMap(): Map<*, *> {
        return JSON.toMap(this)
    }

    fun toJson(): String {
        return JSON.toJson(this)
    }
}

data class GetAllNotificationsResponse(
    val notifications: List<NotificationResponse>
)