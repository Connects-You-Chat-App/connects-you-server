package com.adarsh.connects_you_server.models.common

import com.adarsh.connects_you_server.models.entities.User
import com.adarsh.connects_you_server.models.enums.RoomTypeEnum
import com.adarsh.connects_you_server.utils.JSON
import com.adarsh.connects_you_server.utils.extenstions.safeSlice
import io.vertx.core.json.Json
import java.sql.Timestamp
import java.util.*

data class RoomWithRoomUsers(
    var id: UUID,
    var name: String,
    var type: String,
    var description: String?,
    var logoUrl: String?,
    var createdAt: Date,
    var roomUsers: List<User>,
    var isNewlyCreatedRoom: Boolean? = false,
) {
    companion object {
        fun fromMap(json: Map<String, Any>): RoomWithRoomUsers {
            val roomType = RoomTypeEnum.fromString(json["type"].toString())
            val roomUsers = (json["roomUsers"] as Array<*>).map { user ->
                val userMap = Json.decodeValue(user.toString(), Map::class.java)
                User(
                    id = UUID.fromString(userMap["id"].toString()),
                    name = userMap["name"].toString(),
                    email = userMap["email"].toString(),
                    photoUrl = userMap["photoUrl"]?.toString(),
                    description = userMap["description"]?.toString(),
                    publicKey = userMap["publicKey"].toString(),
                )
            }
            val roomName =
                json["name"]?.toString() ?: if (roomType == RoomTypeEnum.GROUP) {
                    roomUsers.safeSlice(0..2).joinToString(", ") { it.name }
                } else ""
            val roomDescription = json["description"]?.toString()
            val roomLogoUrl = json["logoUrl"]?.toString()

            return RoomWithRoomUsers(
                id = UUID.fromString(json["id"].toString()),
                name = roomName,
                type = roomType.name,
                description = roomDescription,
                logoUrl = roomLogoUrl,
                createdAt = Date.from((json["createdAt"] as Timestamp).toInstant()),
                roomUsers = roomUsers,
            )
        }
    }

    fun toMap(): Map<*, *> {
        return JSON.toMap(this)
    }

    fun toJson(): String {
        return JSON.toJson(this)
    }

}