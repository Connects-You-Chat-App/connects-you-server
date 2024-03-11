package com.adarsh.connectsYouServer.services.v1

import com.adarsh.connectsYouServer.exceptions.NotFoundHttpException
import com.adarsh.connectsYouServer.listeners.SocketIOListener
import com.adarsh.connectsYouServer.models.common.KafkaMessage
import com.adarsh.connectsYouServer.models.common.RoomWithRoomUsers
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.entities.GroupInvitation
import com.adarsh.connectsYouServer.models.entities.Room
import com.adarsh.connectsYouServer.models.entities.RoomUser
import com.adarsh.connectsYouServer.models.entities.User
import com.adarsh.connectsYouServer.models.enums.KafkaMessageType
import com.adarsh.connectsYouServer.models.enums.RoomTypeEnum
import com.adarsh.connectsYouServer.models.enums.RoomUserRoleEnum
import com.adarsh.connectsYouServer.models.enums.SocketEventType
import com.adarsh.connectsYouServer.models.requests.CreateDuetRoomRequest
import com.adarsh.connectsYouServer.models.requests.CreateGroupRoomRequest
import com.adarsh.connectsYouServer.models.requests.JoinGroupRequest
import com.adarsh.connectsYouServer.models.requests.SaveSharedKeyRequest
import com.adarsh.connectsYouServer.models.responses.NotificationResponse
import com.adarsh.connectsYouServer.repositories.RoomRepository
import com.adarsh.connectsYouServer.repositories.RoomUserRepository
import com.adarsh.connectsYouServer.utils.KafkaUtils
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.Date
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class RoomService(
    private val roomRepository: RoomRepository,
    private val roomUserRepository: RoomUserRepository,
    private val kafkaUtils: KafkaUtils,
    private val socketIOServer: SocketIOServer,
    private val sharedKeyService: SharedKeyService,
    private val groupInvitationService: NotificationService,
    private val userService: UserService,
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    /**
     *  1. Check if there is a common room between the 2 users
     *  2. If there is a common room, return that room
     *  3. If there is no common room,
     *          3.1 create a new room and map the 2 users to that room
     *          3.2 save the shared key (key with other user)
     *          3.3 send the room created event to the other user
     *          3.4 send the room created to the current user
     */
    @Transactional
    fun createDuetRoom(
        currentUser: UserJWTClaim,
        createDuetRoomRequest: CreateDuetRoomRequest,
    ): RoomWithRoomUsers {
        val currentUserId = UUID.fromString(currentUser.id)
        val otherUserId = UUID.fromString(createDuetRoomRequest.userId)
        val commonRoom =
            roomRepository.findCommonRoomBetween2UserIds(
                currentUserId,
                otherUserId,
            )
        if (!commonRoom.isNullOrEmpty()) {
            return createDuetRoomResponseWithRespectToOtherUser(
                RoomWithRoomUsers.fromMap(commonRoom),
                currentUser.id,
            )
        }

        val creatorUser = User().apply { id = currentUserId }

        val room =
            roomRepository.save(
                Room(
                    type = RoomTypeEnum.DUET,
                    creatorUser = creatorUser,
                ),
            )

        roomUserRepository.saveAll(
            listOf(
                RoomUser(
                    room = room,
                    user = creatorUser,
                    RoomUserRoleEnum.DUET_CREATOR,
                ),
                RoomUser(
                    room = room,
                    user = User().apply { id = otherUserId },
                    RoomUserRoleEnum.DUET_PARTICIPANT,
                ),
            ),
        )

        var createdRoom: RoomWithRoomUsers? = null

        runBlocking {
            launch {
                sharedKeyService.saveUserKeys(
                    currentUserId,
                    listOf(
                        SaveSharedKeyRequest(
                            key = createDuetRoomRequest.encryptedSharedKey,
                            forUserId = createDuetRoomRequest.userId,
                        ),
                    ),
                )
            }

            launch {
                joinRoom(currentUser.id, room.id.toString())

                createdRoom =
                    fetchRoom(room.id.toString()).apply {
                        this.isNewlyCreatedRoom = true
                    }
            }

            launch {
                kafkaUtils.producer!!.send(
                    kafkaUtils.createCommonProducerRecord(
                        KafkaMessage(
                            eventType = SocketEventType.DUET_ROOM_CREATED,
                            kafkaMessageType = KafkaMessageType.DUET_ROOM_CREATED_MESSAGE,
                            data =
                                mapOf(
                                    "roomId" to room.id.toString(),
                                    "name" to room.name,
                                    "type" to room.type.toString(),
                                    "creatorUser" to
                                        mapOf(
                                            "id" to currentUser.id,
                                            "name" to currentUser.name,
                                        ),
                                    "otherUserId" to createDuetRoomRequest.userId,
                                ),
                        ),
                    ),
                )
            }
        }

        return createDuetRoomResponseWithRespectToOtherUser(
            createdRoom!!,
            currentUser.id,
        )
    }

    /**
     * 1. Create a new room
     * 2. Save the encrypted random key with self userKey
     * 3. Save the same key but encrypted via shared key of other users as group invitations
     * 4. Send the group invitation event to all the other users
     * 5. send the room created to the current user
     */
    @Transactional
    fun createGroupRoom(
        currentUser: UserJWTClaim,
        createGroupRoomRequest: CreateGroupRoomRequest,
    ): RoomWithRoomUsers {
        val currentUserId = UUID.fromString(currentUser.id)

        val room =
            roomRepository.save(
                Room(
                    type = RoomTypeEnum.GROUP,
                    creatorUser = User().apply { id = currentUserId },
                    description = createGroupRoomRequest.description,
                    logoUrl = createGroupRoomRequest.logoUrl,
                    name = createGroupRoomRequest.name,
                ),
            )

        val invitationMessage = "${currentUser.name} invited you to join ${room.name}"

        val groupInvitations =
            createGroupRoomRequest.otherUsersEncryptedRoomSecretKeys.map { otherUsersEncryptedRoomSecretKey ->
                GroupInvitation(
                    room = room,
                    encryptedRoomSecretKey = otherUsersEncryptedRoomSecretKey.encryptedRoomSecretKey,
                    receiverUser = User().apply { id = UUID.fromString(otherUsersEncryptedRoomSecretKey.userId) },
                    senderUser = User().apply { id = currentUserId },
                    message = invitationMessage,
                )
            }

        var createdRoom: RoomWithRoomUsers? = null

        runBlocking {
            launch {
                roomUserRepository.saveAll(
                    listOf(
                        RoomUser(
                            room = room,
                            user = User().apply { id = currentUserId },
                            RoomUserRoleEnum.GROUP_ADMIN,
                        ),
                    ),
                )
            }

            launch {
                joinRoom(currentUser.id, room.id.toString())
                createdRoom =
                    fetchRoom(room.id.toString()).apply {
                        this.isNewlyCreatedRoom = true
                    }
            }

            launch {
                sharedKeyService.saveUserKeys(
                    currentUserId,
                    listOf(
                        SaveSharedKeyRequest(
                            key = createGroupRoomRequest.selfEncryptedRoomSecretKey,
                            forRoomId = room.id.toString(),
                        ),
                    ),
                )
            }

            launch {
                groupInvitationService.saveAll(
                    groupInvitations,
                )
            }
        }

        kafkaUtils.producer!!.send(
            kafkaUtils.createCommonProducerRecord(
                KafkaMessage(
                    eventType = SocketEventType.GROUP_INVITATION,
                    kafkaMessageType = KafkaMessageType.GROUP_INVITATION,
                    data =
                        mapOf(
                            "roomId" to room.id.toString(),
                            "name" to room.name,
                            "type" to room.type.toString(),
                            "description" to room.description,
                            "logoUrl" to room.logoUrl,
                            "senderUserId" to currentUser.id,
                            "message" to invitationMessage,
                            "groupInvitations" to
                                groupInvitations.map { groupInvitation ->
                                    mapOf(
                                        "id" to groupInvitation.id.toString(),
                                        "encryptedRoomSecretKey" to groupInvitation.encryptedRoomSecretKey,
                                        "receiverUserId" to groupInvitation.receiverUser.id.toString(),
                                    )
                                },
                        ),
                ),
            ),
        )

        return createdRoom!!
    }

    /**
     * 1. Find the invitation
     * 2. Save the encrypted random key with self userKey (retrieved by decoding the invitation)
     * 3. Add the user to the room
     * 4. Delete the invitation
     * 5. Send the group joined event to all the other users of same room
     */
    @Transactional
    fun joinGroup(
        currentUser: UserJWTClaim,
        joinGroupRequest: JoinGroupRequest,
    ): RoomWithRoomUsers {
        val currentUserId = UUID.fromString(currentUser.id)
        val invitation =
            groupInvitationService.fetchByInvitationId(UUID.fromString(joinGroupRequest.invitationId)).getOrElse {
                throw NotFoundHttpException()
            }

        var room: RoomWithRoomUsers? = null

        runBlocking {
            launch {
                sharedKeyService.saveUserKeys(
                    currentUserId,
                    listOf(
                        SaveSharedKeyRequest(
                            key = joinGroupRequest.selfEncryptedRoomSecretKey,
                            forRoomId = invitation.room.id.toString(),
                        ),
                    ),
                )
            }

            launch {
                roomUserRepository.save(
                    RoomUser(
                        room = Room().apply { id = invitation.room.id },
                        user = User().apply { id = currentUserId },
                        RoomUserRoleEnum.GROUP_MEMBER,
                    ),
                )
            }

            launch {
                joinRoom(currentUser.id, invitation.room.id.toString())

                room = fetchRoom(invitation.room.id.toString()).apply { this.isNewlyCreatedRoom = true }
            }

            launch {
                groupInvitationService.deleteByInvitationId(invitation.id)
            }
        }

        kafkaUtils.producer!!.send(
            kafkaUtils.createCommonProducerRecord(
                KafkaMessage(
                    eventType = SocketEventType.GROUP_JOINED,
                    kafkaMessageType = KafkaMessageType.GROUP_JOINED,
                    data =
                        mapOf(
                            "roomId" to invitation.room.id.toString(),
                            "userId" to currentUser.id,
                        ),
                ),
            ),
        )

        return room!!
    }

    fun fetchRooms(currentUser: UserJWTClaim): List<RoomWithRoomUsers> {
        val response = roomRepository.findRoomsByUserId(UUID.fromString(currentUser.id))
        return response.map {
            val roomWithRoomUsers = RoomWithRoomUsers.fromMap(it)
            if (roomWithRoomUsers.type == RoomTypeEnum.DUET.name) {
                createDuetRoomResponseWithRespectToOtherUser(roomWithRoomUsers, currentUser.id)
            } else {
                roomWithRoomUsers
            }
        }
    }

    fun fetchOnlyRoomIdsByUserId(userId: String): List<String> {
        val cachedRoomIds = redisTemplate.opsForValue().get("room_ids:$userId") as List<String>? ?: emptyList<String>()
        if (cachedRoomIds.isNotEmpty()) {
            return cachedRoomIds
        } else {
            val roomIds = roomRepository.findOnlyRoomIdsByUserId(UUID.fromString(userId))
            redisTemplate.opsForValue().set("room_ids:$userId", roomIds, Duration.ofHours(2)) // Cache for 1 hour
            return roomIds
        }
    }

    private fun fetchRoom(roomId: String): RoomWithRoomUsers {
        val response = roomRepository.findRoomByRoomId(UUID.fromString(roomId))
        return RoomWithRoomUsers.fromMap(response)
    }

    private fun createDuetRoomResponseWithRespectToOtherUser(
        roomWithRoomUsers: RoomWithRoomUsers,
        userId: String,
    ): RoomWithRoomUsers {
        if (roomWithRoomUsers.type == RoomTypeEnum.DUET.name) {
            roomWithRoomUsers.roomUsers.find {
                it.id.toString() != userId
            }?.let {
                roomWithRoomUsers.name = it.name
                roomWithRoomUsers.description = it.description
                roomWithRoomUsers.logoUrl = it.photoUrl
            }
        }
        return roomWithRoomUsers
    }

    fun sendDuetRoomCreatedEvent(kafkaMessage: KafkaMessage) {
        val data = kafkaMessage.data!!
        val roomId = data["roomId"].toString()
        val otherUserId = data["otherUserId"].toString()
        val roomData =
            fetchRoom(roomId).apply {
                this.isNewlyCreatedRoom = true
            }
        val userSessions = SocketIOListener.getUserSessions(otherUserId)

        val clients =
            userSessions.map {
                mapOf(
                    "userId" to otherUserId,
                    "client" to socketIOServer.getClient(UUID.fromString(it)),
                )
            }

        clients.forEach {
            val userId = it["userId"] as String
            val client = it["client"] as SocketIOClient
            client.joinRoom(roomId)
            client.sendEvent(
                kafkaMessage.eventType.name,
                createDuetRoomResponseWithRespectToOtherUser(roomData, userId).toMap(),
            )
        }
    }

    fun sendGroupRoomInvitationEvent(kafkaMessage: KafkaMessage) {
        val data = kafkaMessage.data as MutableMap<String, Any?>
        val roomId = data["roomId"].toString()
        val groupInvitations = data["groupInvitations"] as List<Map<String, String>>
        val senderUser = userService.fetchUserById(UUID.fromString(data["senderUserId"].toString())).get()
        data.apply {
            this["senderUser"] = senderUser.toSendableMap()
        }

        val clients =
            groupInvitations.map { groupInvitation ->
                val userId = groupInvitation["receiverUserId"].toString()
                val userSessions = SocketIOListener.getUserSessions(userId)
                userSessions.map {
                    mapOf(
                        "invitation" to groupInvitation,
                        "client" to socketIOServer.getClient(UUID.fromString(it)),
                    )
                }
            }.flatten()

        clients.forEach {
            val invitation = it["invitation"] as Map<String, String>
            val client = it["client"] as SocketIOClient
            client.joinRoom(roomId)
            client.sendEvent(
                kafkaMessage.eventType.name,
                NotificationResponse.fromGroupNotification(
                    GroupInvitation(
                        id = UUID.fromString(invitation["id"]),
                        message = data["message"].toString(),
                        room = Room().apply { id = UUID.fromString(roomId) },
                        senderUser = senderUser,
                        receiverUser = User().apply { id = UUID.fromString(invitation["receiverUserId"]) },
                        encryptedRoomSecretKey = invitation["encryptedRoomSecretKey"].toString(),
                        sendAt = Date(),
                    ),
                ).toMap(),
            )
        }
    }

    fun sendGroupJoinedEvent(kafkaMessage: KafkaMessage) {
        val data = kafkaMessage.data as MutableMap<String, Any?>
        val roomId = data["roomId"].toString()
        val user = userService.fetchUserById(UUID.fromString(data["userId"].toString())).get()

        socketIOServer.getRoomOperations(roomId).sendEvent(
            kafkaMessage.eventType.name,
            data.apply {
                this["user"] = user.toSendableMap()
            },
        )
    }

    fun joinRoom(
        userId: String,
        roomId: String,
    ) {
        val userSessions = SocketIOListener.getUserSessions(userId)

        val clients =
            userSessions.map {
                socketIOServer.getClient(UUID.fromString(it))
            }

        clients.forEach {
            it.joinRoom(roomId)
        }
    }

    fun fetchUpdatedDataAfter(
        userId: UUID,
        roomIds: List<UUID>,
        updatedAt: Date,
    ): List<RoomWithRoomUsers> {
        val rooms = roomRepository.findRoomsByRoomIdsAfter(roomIds, updatedAt)
        return rooms.map {
            val room = RoomWithRoomUsers.fromMap(it)
            if (room.type == RoomTypeEnum.DUET.name) {
                createDuetRoomResponseWithRespectToOtherUser(room, userId.toString())
            } else {
                room
            }
        }
    }
}
