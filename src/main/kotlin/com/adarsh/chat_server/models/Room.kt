package com.adarsh.chat_server.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity(name = "rooms")
data class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    val name: String? = null,

    val logoUrl: String? = null,

    val description: String? = null,

    @Enumerated(EnumType.STRING)
    val type: RoomTypeEnum,

    @ManyToOne(targetEntity = User::class)
    val creatorUser: User,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: Date = Date(),

    @UpdateTimestamp
    val updatedAt: Date = Date(),
)