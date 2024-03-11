package com.adarsh.connectsYouServer.models.entities

import com.adarsh.connectsYouServer.utils.JSON
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "group_invitations")
data class GroupInvitation(
    @Id
    var id: UUID = UUID.randomUUID(),
    @ManyToOne(targetEntity = Room::class)
    var room: Room,
    @ManyToOne(targetEntity = User::class)
    var senderUser: User,
    @ManyToOne(targetEntity = User::class)
    var receiverUser: User,
    var message: String,
    var encryptedRoomSecretKey: String,
    @get:JsonProperty("isAccepted")
    var isAccepted: Boolean = false,
    var sendAt: Date = Date(),
) {
    constructor() : this(UUID.randomUUID(), Room(), User(), User(), "", "", false, Date())

    fun toMap(): Map<*, *> {
        return JSON.toMap(this)
    }

    fun toJson(): String {
        return JSON.toJson(this)
    }

    companion object {
        fun fromJson(json: String): GroupInvitation {
            return JSON.fromJson(json, GroupInvitation::class.java)
        }
    }
}
