package com.adarsh.connectsYouServer.models.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.ManyToOne
import java.io.Serializable
import java.util.Date
import java.util.UUID

@Entity(name = "message_status")
@IdClass(MessageStatusKey::class)
data class MessageStatus(
    @Id
    @ManyToOne(targetEntity = Message::class)
    var message: Message,
    @Id
    @ManyToOne(targetEntity = User::class)
    var user: User,
    var isDelivered: Boolean = false,
    var isRead: Boolean = false,
    var deliveredAt: Date? = null,
    var readAt: Date? = null,
) {
    constructor() : this(Message(), User())
}

class MessageStatusKey : Serializable {
    lateinit var message: UUID
    lateinit var user: UUID
}