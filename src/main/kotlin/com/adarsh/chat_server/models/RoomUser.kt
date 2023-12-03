package com.adarsh.chat_server.models

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity(name = "room_users")
@IdClass(RoomUserKey::class)
data class RoomUser(
    @Id
    @ManyToOne(targetEntity = Room::class)
    val room: Room,

    @Id
    @ManyToOne(targetEntity = User::class)
    val user: User,

    @Enumerated(EnumType.STRING)
    val userRole: RoomUserRoleEnum,

    val joinedAt: Date = Date(),
)

class RoomUserKey : Serializable {
    lateinit var room: UUID
    lateinit var user: UUID
}