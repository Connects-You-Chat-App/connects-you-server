package com.adarsh.connectsYouServer.services.v1

import com.adarsh.connectsYouServer.configs.SocketIOConfig
import com.adarsh.connectsYouServer.models.common.KafkaMessage
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.entities.Message
import com.adarsh.connectsYouServer.models.entities.MessageStatus
import com.adarsh.connectsYouServer.models.entities.Room
import com.adarsh.connectsYouServer.models.entities.User
import com.adarsh.connectsYouServer.models.enums.KafkaMessageType
import com.adarsh.connectsYouServer.models.enums.MessageTypeEnum
import com.adarsh.connectsYouServer.models.enums.SocketEventType
import com.adarsh.connectsYouServer.models.requests.EditMessageRequest
import com.adarsh.connectsYouServer.models.requests.SendMessageRequest
import com.adarsh.connectsYouServer.models.responses.MessageResponse
import com.adarsh.connectsYouServer.repositories.MessageRepository
import com.adarsh.connectsYouServer.repositories.MessageStatusRepository
import com.adarsh.connectsYouServer.utils.KafkaUtils
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.stereotype.Service
import java.util.*

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val messageStatusRepository: MessageStatusRepository,
    private val kafkaUtils: KafkaUtils,
    private val socketIOServer: SocketIOServer,
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
                            Room().apply {
                                id = UUID.fromString(it)
                            }
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
        userId: UUID,
        roomIds: List<UUID>,
        updatedAt: Date,
    ) = messageRepository.findMessagesByRoomIdsAfter(
        userId,
        roomIds,
        updatedAt,
    )

    fun updateMessageStatusesToDelivered(
        messageId: String,
        userIds: List<String>,
    ) {
        val date = Date()
        messageStatusRepository.saveAll(
            userIds.map {
                MessageStatus().apply {
                    message = Message().apply { id = UUID.fromString(messageId) }
                    user = User().apply { id = UUID.fromString(it) }
                    isDelivered = true
                    deliveredAt = date
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
                            "messageIds" to listOf(messageId),
                            "userIds" to userIds,
                            "date" to date,
                        ),
                ),
            ),
        )
    }

    fun updateMessageStatusesToRead(
        readUser: UserJWTClaim,
        messageIds: List<UUID>,
    ) {
        val messageIdsSet = messageIds.toSet()
        val userId = UUID.fromString(readUser.id)
        val date = Date()
        val statuses = messageStatusRepository.findAllByMessageIdInAndUserId(messageIds, userId)
        val existingStatusSet = mutableSetOf<UUID>()
        var actualUpdateCount = 0
        statuses.forEach {
            if (!it.isRead) {
                actualUpdateCount++
                it.isRead = true
                it.readAt = date
            }
            existingStatusSet.add(it.message.id)
        }
        val missingMessageIds = messageIdsSet.minus(existingStatusSet)
        if (missingMessageIds.isNotEmpty()) {
            messageStatusRepository.saveAll(
                missingMessageIds.map {
                    MessageStatus().apply {
                        message = Message().apply { id = it }
                        user = User().apply { id = userId }
                        isRead = true
                        readAt = date
                        isDelivered = true
                        deliveredAt = date
                    }
                },
            )

            actualUpdateCount = messageIds.size
        } else {
            if (actualUpdateCount > 0) {
                messageStatusRepository.saveAll(statuses)
            }
        }

        if (actualUpdateCount > 0) {
            kafkaUtils.producer!!.send(
                kafkaUtils.createCommonProducerRecord(
                    KafkaMessage(
                        SocketEventType.ROOM_MESSAGE_READ,
                        KafkaMessageType.ROOM_MESSAGE_READ,
                        mapOf(
                            "messageIds" to messageIds.map { it.toString() },
                            "userIds" to listOf(readUser.id),
                            "date" to date,
                        ),
                    ),
                ),
            )
        }
    }

    fun updateMessageStatusesToDelivered(
        deliveredUser: UserJWTClaim,
        messageIds: List<UUID>,
    ) {
        val messageIdsSet = messageIds.toSet()
        val userId = UUID.fromString(deliveredUser.id)
        val date = Date()
        val statuses = messageStatusRepository.findAllByMessageIdInAndUserId(messageIds, userId)
        val existingStatusSet = mutableSetOf<UUID>()
        println("statuses: $statuses")
        var actualUpdateCount = 0
        statuses.forEach {
            if (!it.isDelivered) {
                actualUpdateCount++
                it.isDelivered = true
                it.deliveredAt = date
            }
            existingStatusSet.add(it.message.id)
        }

        val missingMessageIds = messageIdsSet.minus(existingStatusSet)
        if (missingMessageIds.isNotEmpty()) {
            messageStatusRepository.saveAll(
                missingMessageIds.map {
                    MessageStatus().apply {
                        message = Message().apply { id = it }
                        user = User().apply { id = userId }
                        isDelivered = true
                        deliveredAt = date
                    }
                },
            )

            actualUpdateCount = messageIds.size
        } else {
            if (actualUpdateCount > 0) {
                messageStatusRepository.saveAll(statuses)
            }
        }

        if (actualUpdateCount > 0) {
            kafkaUtils.producer!!.send(
                kafkaUtils.createCommonProducerRecord(
                    KafkaMessage(
                        SocketEventType.ROOM_MESSAGE_DELIVERED,
                        KafkaMessageType.ROOM_MESSAGE_DELIVERED,
                        mapOf(
                            "messageIds" to messageIds.map { it.toString() },
                            "userIds" to listOf(deliveredUser.id),
                            "date" to date,
                        ),
                    ),
                ),
            )
        }
    }

    fun sendRoomMessageStatusEvent(kafkaMessage: KafkaMessage) {
        val messageIds = kafkaMessage.data!!["messageIds"] as List<String>
        val userIdSet =
            messageRepository.findSenderUserIdByMessageIdsIn(messageIds.map { UUID.fromString(it) })
                .map { it.toString() }.toSet()
        socketIOServer.allClients.filter {
            val user = SocketIOConfig.getUserJWTClaim(it.handshakeData)!!
            userIdSet.contains(user.id)
        }.map {
            it.sendEvent(kafkaMessage.eventType.toString(), kafkaMessage.data)
        }
    }
}
