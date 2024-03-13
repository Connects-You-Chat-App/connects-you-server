package com.adarsh.connectsYouServer.services.v1

import com.adarsh.connectsYouServer.models.common.KafkaMessage
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.entities.Message
import com.adarsh.connectsYouServer.models.entities.MessageStatus
import com.adarsh.connectsYouServer.models.entities.Room
import com.adarsh.connectsYouServer.models.entities.User
import com.adarsh.connectsYouServer.models.enums.KafkaMessageType
import com.adarsh.connectsYouServer.models.enums.KafkaMessageType.ROOM_MESSAGE_READ
import com.adarsh.connectsYouServer.models.enums.MessageTypeEnum
import com.adarsh.connectsYouServer.models.enums.SocketEventType
import com.adarsh.connectsYouServer.models.requests.EditMessageRequest
import com.adarsh.connectsYouServer.models.requests.SendMessageRequest
import com.adarsh.connectsYouServer.models.responses.MessageResponse
import com.adarsh.connectsYouServer.repositories.MessageRepository
import com.adarsh.connectsYouServer.repositories.MessageStatusRepository
import com.adarsh.connectsYouServer.utils.KafkaUtils
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val messageStatusRepository: MessageStatusRepository,
    private val kafkaUtils: KafkaUtils,
    messageStatus: MessageStatusRepository,
) {
    fun fetchMessagesByRoomId(roomId: UUID) = messageRepository.findByRoomId(roomId)

    fun sendMessage(
        user: UserJWTClaim,
        sendMessageRequest: SendMessageRequest,
    ) {
        val message =
            messageRepository.save(
                Message().apply {
                    id = UUID.fromString(sendMessageRequest.messageId)
                    room = Room().apply { id = UUID.fromString(sendMessageRequest.roomId) }
                    message = sendMessageRequest.message
                    type = MessageTypeEnum.fromString(sendMessageRequest.type)
                    senderUser = User().apply { id = UUID.fromString(user.id) }
                    belongsToMessage =
                        sendMessageRequest.belongsToMessageId?.let {
                            Message().apply { id = UUID.fromString(it) }
                        }
                    forwardedFromRoom =
                        sendMessageRequest.forwardedFromRoomId?.let {
                            Room().apply { id = UUID.fromString(it) }
                        }
                },
            )
        print("incoming messageId: ${sendMessageRequest.messageId} and saved messageId: ${message.id}")
        kafkaUtils.producer!!.send(
            kafkaUtils.createRoomProducerRecord(
                sendMessageRequest.roomId,
                KafkaMessage(
                    eventType = SocketEventType.ROOM_MESSAGE,
                    data = MessageResponse.fromMessage(message).toMap(),
                ),
            ),
        )
    }

    fun editMessage(
        user: UserJWTClaim,
        editMessageRequest: EditMessageRequest,
    ) {
        val message =
            messageRepository.findById(UUID.fromString(editMessageRequest.messageId)).get()

        if (message.senderUser.id.toString() != user.id) {
            throw Exception("You are not the sender of this message")
        }
        if (message.isDeleted) throw Exception("Message is deleted")
        if (message.forwardedFromRoom != null) throw Exception("Message is forwarded")
        if (message.type != MessageTypeEnum.TEXT) throw Exception("Message is not a text message")
        if (message.createdAt > Date(System.currentTimeMillis() - 1000 * 60 * 5)) {
            throw Exception("Message is too old to edit")
        }

        message.message = editMessageRequest.message
        message.editedAt = Date()
        messageRepository.save(message)
        kafkaUtils.producer!!.send(
            kafkaUtils.createRoomProducerRecord(
                message.room.id.toString(),
                KafkaMessage(
                    eventType = SocketEventType.ROOM_MESSAGE_EDIT,
                    data = MessageResponse.fromMessage(message).toMap(),
                ),
            ),
        )
    }

    fun fetchMessagesByRoomIdsAfter(
        roomIds: List<UUID>,
        updatedAt: Date,
    ) = messageRepository.findMessagesByRoomIdsAfter(
        roomIds,
        updatedAt,
    )

    fun updateMessageStatusesToDelivered(
        messageId: String,
        userIds: List<String>,
        roomId: String,
    ) {
        messageStatusRepository.saveAll(
            userIds.map {
                MessageStatus().apply {
                    message = Message().apply { id = UUID.fromString(messageId) }
                    user = User().apply { id = UUID.fromString(it) }
                    delivered = true
                    deliveredAt = Date()
                }
            },
        )

        kafkaUtils.producer!!.send(
            kafkaUtils.createCommonProducerRecord(
                KafkaMessage(
                    SocketEventType.ROOM_MESSAGE_DELIVERED,
                    KafkaMessageType.ROOM_MESSAGE_DELIVERED,
                    data =
                        mapOf(
                            "roomId" to roomId,
                            "messageId" to messageId,
                            "userIds" to userIds,
                        ),
                ),
            ),
        )
    }

    fun updateMessageStatusesToRead(
        readUser: UserJWTClaim,
        roomId: String,
        messageIds: List<UUID>,
    ) {
        val userId = UUID.fromString(readUser.id)
        messageStatusRepository.findAllByMessageIdInAndUserId(messageIds, userId).forEach {
            it.read = true
            it.readAt = Date()
        }
        if (messageStatusRepository.findAllByMessageIdInAndUserId(messageIds, userId).isEmpty()) {
            messageStatusRepository.saveAll(
                messageIds.map {
                    MessageStatus().apply {
                        message = Message().apply { id = it }
                        user = User().apply { id = userId }
                        read = true
                        readAt = Date()
                        delivered = true
                        deliveredAt = Date()
                    }
                },
            )
        } else {
            messageStatusRepository.saveAll(messageStatusRepository.findAllByMessageIdInAndUserId(messageIds, userId))
        }

        kafkaUtils.producer!!.send(
            kafkaUtils.createCommonProducerRecord(
                KafkaMessage(
                    SocketEventType.ROOM_MESSAGE_READ,
                    ROOM_MESSAGE_READ,
                    mapOf(
                        "messageIds" to messageIds.map { it.toString() },
                        "userId" to readUser.id,
                        "roomId" to roomId,
                    ),
                ),
            ),
        )
    }
}