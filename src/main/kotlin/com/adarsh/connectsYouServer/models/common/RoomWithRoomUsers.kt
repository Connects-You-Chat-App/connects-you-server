package com.adarsh.connectsYouServer.models.common

import com.adarsh.connectsYouServer.models.entities.User
import com.adarsh.connectsYouServer.models.enums.RoomTypeEnum
import com.adarsh.connectsYouServer.utils.JSON
import com.adarsh.connectsYouServer.utils.extenstions.safeSlice
import io.vertx.core.json.Json
import java.sql.Timestamp
import java.util.Date
import java.util.UUID

data class RoomWithRoomUsers(
    var id: UUID,
    var name: String,
    var type: String,
    var description: String?,
    var logoUrl: String?,
    var createdAt: Date,
    var updatedAt: Date,
    var roomUsers: List<User>,
    var isNewlyCreatedRoom: Boolean? = false,
) {
    companion object {
        fun fromMap(json: Map<String, Any>): RoomWithRoomUsers {
            val roomType = RoomTypeEnum.fromString(json["type"].toString())
            val roomUsers =
                (json["roomUsers"] as Array<*>).map { user ->
                    val userMap = Json.decodeValue(user.toString(), Map::class.java)
                    User(
                        id = UUID.fromString(userMap["id"].toString()),
                        name = userMap["name"].toString(),
                        email = userMap["email"].toString(),
                        photoUrl = if (userMap["photoUrl"] != null) userMap["photoUrl"].toString() else null,
                        description = if (userMap["description"] != null) userMap["description"].toString() else null,
                        publicKey = userMap["publicKey"].toString(),
                    )
                }
            val roomName =
                json["name"]?.toString() ?: if (roomType == RoomTypeEnum.GROUP) {
                    roomUsers.safeSlice(0..2).joinToString(", ") { it.name }
                } else {
                    ""
                }
            val roomDescription = if (json["description"] != null) json["description"].toString() else null
            val roomLogoUrl = if (json["logoUrl"] != null) json["logoUrl"].toString() else null

            return RoomWithRoomUsers(
                id = UUID.fromString(json["id"].toString()),
                name = roomName,
                type = roomType.name,
                description = roomDescription,
                logoUrl = roomLogoUrl,
                createdAt = Date.from((json["createdAt"] as Timestamp).toInstant()),
                updatedAt = Date.from((json["updatedAt"] as Timestamp).toInstant()),
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