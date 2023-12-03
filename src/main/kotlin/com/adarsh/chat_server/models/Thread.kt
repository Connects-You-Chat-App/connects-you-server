package com.adarsh.chat_server.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity(name = "threads")
data class Thread(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    @ManyToOne(targetEntity = Room::class)
    val room: Room,

    @ManyToOne(targetEntity = Message::class)
    val message: Message,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: Date,

    @UpdateTimestamp
    val updatedAt: Date
)
