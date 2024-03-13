package com.adarsh.connectsYouServer.models.entities

import com.adarsh.connectsYouServer.models.enums.MessageTypeEnum
import com.adarsh.connectsYouServer.utils.JSON
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.Date
import java.util.UUID

@Entity(name = "messages")
data class Message(
    @Id
    var id: UUID = UUID.randomUUID(),
    @ManyToOne(targetEntity = Room::class)
    var room: Room,
    @ManyToOne(targetEntity = User::class)
    var senderUser: User,
    var message: String,
    @Enumerated(EnumType.STRING)
    var type: MessageTypeEnum,
    @ManyToOne(targetEntity = Message::class)
    var belongsToMessage: Message? = null,
    @get:JsonProperty("isDeleted")
    var isDeleted: Boolean = false,
    @ManyToOne(targetEntity = Room::class)
    var forwardedFromRoom: Room? = null,
    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: Date = Date(),
    @UpdateTimestamp
    var updatedAt: Date = Date(),
    var editedAt: Date? = null,
) {
    constructor() : this(UUID.randomUUID(), Room(), User(), "", MessageTypeEnum.TEXT)

    companion object {
        fun fromMap(json: Map<String, *>): Message {
            return JSON.fromMap(json, Message::class.java)
        }
    }

    fun toMap(): Map<*, *> {
        return JSON.toMap(this)
    }
}