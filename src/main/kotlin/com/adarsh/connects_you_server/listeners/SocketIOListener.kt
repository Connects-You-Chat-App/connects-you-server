package com.adarsh.connects_you_server.listeners

import com.adarsh.connects_you_server.configs.SocketIOConfig
import com.adarsh.connects_you_server.models.common.KafkaMessage
import com.adarsh.connects_you_server.models.enums.KafkaMessageType
import com.adarsh.connects_you_server.models.enums.SocketEventType
import com.adarsh.connects_you_server.services.v1.RoomService
import com.adarsh.connects_you_server.utils.KafkaUtils
import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class SocketIOListener(
    private val socketIOServer: SocketIOServer,
    private val kafkaUtils: KafkaUtils,
    private val kafkaConsumers: KafkaConsumers,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val roomService: RoomService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private val userSessions = mutableMapOf<String, Set<String>>()

        fun getUserSessions(userId: String): Set<String> {
            return userSessions[userId] ?: setOf()
        }
    }

    init {
        socketIOServer.addConnectListener {
            onConnect(it)
        }
        socketIOServer.addDisconnectListener { onDisconnect(it) }
        addEventListeners()
        addConsumers()
    }

    private final fun addConsumers() {
        kafkaConsumers.consumeRoom {
            if (it.data != null) {
                socketIOServer.getRoomOperations(it.data!!["roomId"].toString())
                    .sendEvent(it.eventType.toString(), it.data)
            }
        }
        kafkaConsumers.consumeBroadcast {
            if (it.data != null) {
                socketIOServer.broadcastOperations.sendEvent(it.eventType.toString(), it.data)
            }
        }
        kafkaConsumers.consumeCommon {
            if (it.data != null) {
                consumerCommonMessages(it)
            }
        }
    }

    private final fun consumerCommonMessages(kafkaMessage: KafkaMessage) {
        when (kafkaMessage.kafkaMessageType) {
            KafkaMessageType.DUET_ROOM_CREATED_MESSAGE -> {
                roomService.sendDuetRoomCreatedEvent(kafkaMessage)
            }

            KafkaMessageType.GROUP_INVITATION -> {
                roomService.sendGroupRoomInvitationEvent(kafkaMessage)
            }

            KafkaMessageType.GROUP_JOINED -> {
                roomService.sendGroupJoinedEvent(kafkaMessage)
            }

            else -> {
                return
            }
        }
    }

    private final fun addEventListeners() {
        socketIOServer.addEventListener("event", String::class.java, onEvent())
    }

    private final fun onConnect(client: SocketIOClient) {
        val user = SocketIOConfig.getUserJWTClaim(client.handshakeData)!!
        if (userSessions[user.id]?.contains(client.sessionId.toString()) == true) {
            return
        }
        userSessions[user.id] =
            userSessions[user.id]?.plus(client.sessionId.toString()) ?: setOf(client.sessionId.toString())
        logger.info("Connected with client: ${client.remoteAddress}")
        redisTemplate.opsForValue().set("user-presence:${user.id}", true)
        joinRooms(client)
        kafkaUtils.producer!!.send(
            kafkaUtils.createBroadcastProducerRecord(
                KafkaMessage(
                    SocketEventType.USER_PRESENCE,
                    data = mapOf(
                        "userId" to user.id,
                        "value" to true,
                    )
                )
            )
        )
    }

    private fun joinRooms(client: SocketIOClient) {
        val user = SocketIOConfig.getUserJWTClaim(client.handshakeData)!!
        val rooms = roomService.fetchOnlyRoomIdsByUserId(user.id)
        rooms.forEach {
            client.joinRoom(it)
        }
        logger.info("Joined rooms: $rooms")
    }

    private final fun onDisconnect(client: SocketIOClient) {
        logger.info("Disconnected with client: ${client.remoteAddress}")
        val user = SocketIOConfig.getUserJWTClaim(client.handshakeData)!!
        val time = Date().toInstant().toString()
        redisTemplate.opsForValue().set("user-presence:${user.id}", time)
        kafkaUtils.producer!!.send(
            kafkaUtils.createBroadcastProducerRecord(
                KafkaMessage(
                    SocketEventType.USER_PRESENCE,
                    data = mapOf(
                        "userId" to user.id,
                        "value" to time,
                    )
                )
            )
        )
        userSessions[user.id] = userSessions[user.id]?.minus(client.sessionId.toString()) ?: setOf()
        SocketIOConfig.removeUserJWTClaim(client.handshakeData)
    }

    private final fun onEvent() = fun(client: SocketIOClient, data: String, ackRequest: AckRequest) {
        logger.info("Received data: $data from client: ${client.remoteAddress}")
        ackRequest.sendAckData("Server received data!")
    }
}