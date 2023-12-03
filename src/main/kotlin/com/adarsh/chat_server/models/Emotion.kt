package com.adarsh.chat_server.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.util.*

@Entity
data class Emotion(
    @Id
    val id: UUID,
    val emotion: String,

    @ManyToOne(targetEntity = User::class)
    val senderUser: User,

    @ManyToOne(targetEntity = Message::class)
    val message: Message,

    val sendAt: Date = Date(),
)
