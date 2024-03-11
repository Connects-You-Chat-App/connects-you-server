package com.adarsh.connectsYouServer.services.v1

import com.adarsh.connectsYouServer.models.common.RoomWithRoomUsers
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.entities.UserSharedKey
import com.adarsh.connectsYouServer.models.responses.GetUserSharedKeysResponse
import io.vertx.core.json.Json
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class CommonService(
    private val sharedKeyService: SharedKeyService,
    private val roomService: RoomService,
    private val messageService: MessageService,
) {
    fun getUpdatedData(
        user: UserJWTClaim,
        updatedAt: Date,
    ): Map<String, Any> {
        println(updatedAt.toInstant().toString())

        val userId = UUID.fromString(user.id)
        var sharedKeys: List<UserSharedKey>
        var rooms: List<RoomWithRoomUsers>
        var messages: List<Map<*, *>>

        runBlocking {
            val (sharedKeysResponse, roomIds) =
                async {
                    val sharedKeysResponse =
                        async {
                            sharedKeyService.fetchUpdatedDataAfter(userId, updatedAt)
                        }
                    val roomIdsResponse =
                        async {
                            roomService.fetchOnlyRoomIdsByUserId(user.id)
                        }

                    Pair(sharedKeysResponse.await(), roomIdsResponse.await())
                }.await()
            val roomUUIDs = roomIds.map { UUID.fromString(it) }
            println("roomUUIDs: $roomUUIDs")
            val roomsAsync = async { roomService.fetchUpdatedDataAfter(userId, roomUUIDs, updatedAt) }
            val messagesAsync =
                async {
                    messageService.fetchMessagesByRoomIdsAfter(roomUUIDs, updatedAt)
                }

            sharedKeys = sharedKeysResponse
            rooms = roomsAsync.await()
            messages = messagesAsync.await()
            println("rooms: $rooms")
        }

        val messageList =
            messages.map<Map<*, *>, Map<*, *>> {
                val roomArr = it["room"] as Array<*>
                val usersArr = it["users"] as Array<*>
                val room = Json.decodeValue(roomArr[0].toString(), Map::class.java)
                val senderUserId = it["senderUserId"]
                var otherUser: Map<*, *>? = null
                var senderUser: Map<*, *>? = null
                usersArr.forEach { user ->
                    val userMap = Json.decodeValue(user.toString(), Map::class.java)
                    if (userMap["id"].toString() == senderUserId.toString()) {
                        senderUser = userMap
                    } else {
                        otherUser = userMap
                    }
                }

                mapOf(
                    "id" to it["id"],
                    "message" to it["message"],
                    "createdAt" to it["createdAt"],
                    "updatedAt" to it["updatedAt"],
                    "type" to it["type"],
                    "belongsToMessageId" to it["belongsToMessageId"],
                    "isDeleted" to it["isDeleted"],
                    "forwardedFromRoomId" to it["forwardedFromRoomId"],
                    "senderUser" to senderUser,
                    "editedAt" to it["editedAt"],
                    "room" to
                        mapOf(
                            "id" to room["id"],
                            "name" to room["name"],
                            "type" to room["type"],
                            "updatedAt" to room["updatedAt"],
                        ),
                    "otherUser" to otherUser,
                )
            }

        return mapOf(
            "sharedKeys" to GetUserSharedKeysResponse.fromListOfUserSharedKey(sharedKeys).keys,
            "rooms" to rooms,
            "messages" to messageList,
        )
    }
}