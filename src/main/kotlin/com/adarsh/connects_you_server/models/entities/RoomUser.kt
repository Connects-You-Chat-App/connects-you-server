package com.adarsh.connects_you_server.models.entities

import com.adarsh.connects_you_server.models.enums.RoomUserRoleEnum
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity(name = "room_users")
@IdClass(RoomUserKey::class)
data class RoomUser(
    @JsonIgnore
    @Id
    @ManyToOne(targetEntity = Room::class)
    var room: Room,

    @Id
    @ManyToOne(targetEntity = User::class)
    var user: User,

    @Enumerated(EnumType.STRING)
    var userRole: RoomUserRoleEnum,

    var joinedAt: Date = Date(),
) {
    constructor() : this(Room(), User(), RoomUserRoleEnum.GROUP_MEMBER)
}

class RoomUserKey : Serializable {
    lateinit var room: UUID
    lateinit var user: UUID
}