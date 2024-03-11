package com.adarsh.connectsYouServer.models.entities

import com.adarsh.connectsYouServer.models.enums.RoomUserRoleEnum
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.ManyToOne
import java.io.Serializable
import java.util.Date
import java.util.UUID

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
