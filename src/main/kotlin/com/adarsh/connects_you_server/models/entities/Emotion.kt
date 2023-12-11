package com.adarsh.connects_you_server.models.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.util.*

@Entity
data class Emotion(
    @Id
    var id: UUID,
    var emotion: String,

    @ManyToOne(targetEntity = User::class)
    var senderUser: User,

    @ManyToOne(targetEntity = Message::class)
    var message: Message,

    var sendAt: Date = Date(),
) {
    constructor() : this(UUID.randomUUID(), "", User(), Message())
}