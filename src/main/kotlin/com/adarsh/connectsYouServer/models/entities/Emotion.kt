package com.adarsh.connectsYouServer.models.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.util.Date
import java.util.UUID

@Entity
data class Emotion(
    @Id
    var id: UUID = UUID.randomUUID(),
    var emotion: String,
    @ManyToOne(targetEntity = User::class)
    var senderUser: User,
    @ManyToOne(targetEntity = Message::class)
    var message: Message,
    var sendAt: Date = Date(),
) {
    constructor() : this(UUID.randomUUID(), "", User(), Message())
}
