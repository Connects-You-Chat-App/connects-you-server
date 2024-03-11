package com.adarsh.connectsYouServer.models.responses

import com.adarsh.connectsYouServer.models.entities.Message
import com.adarsh.connectsYouServer.utils.JSON
import com.fasterxml.jackson.annotation.JsonProperty

data class MessageResponse(
    val id: String,
    val roomId: String,
    val senderUser: MessageUser,
    val message: String,
    val type: String,
    val belongsToMessage: MessageResponse?,
    @get:JsonProperty("isDeleted")
    val isDeleted: Boolean,
    val forwardedFromRoomId: String?,
    val createdAt: String,
    val updatedAt: String,
    val editedAt: String?,
) {
    data class MessageUser(
        val id: String,
        val name: String,
        val email: String,
        val publicKey: String,
        val photoUrl: String?,
    )

    companion object {
        fun fromMessage(message: Message): MessageResponse {
            return MessageResponse(
                id = message.id.toString(),
                roomId = message.room.id.toString(),
                senderUser =
                    MessageUser(
                        id = message.senderUser.id.toString(),
                        name = message.senderUser.name,
                        email = message.senderUser.email,
                        publicKey = message.senderUser.publicKey,
                        photoUrl = message.senderUser.photoUrl,
                    ),
                message = message.message,
                type = message.type.toString(),
                belongsToMessage = message.belongsToMessage?.let { fromMessage(it) },
                isDeleted = message.isDeleted,
                forwardedFromRoomId = message.forwardedFromRoom?.id.toString(),
                createdAt = message.createdAt.toInstant().toString(),
                updatedAt = message.updatedAt.toInstant().toString(),
                editedAt = message.editedAt?.toInstant()?.toString(),
            )
        }
    }

    fun toMap(): Map<*, *> {
        return JSON.toMap(this)
    }
}

data class RoomMessagesResponse(
    val roomId: String,
    val messages: List<MessageResponse>,
)
