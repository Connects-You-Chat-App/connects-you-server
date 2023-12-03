package com.adarsh.chat_server.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    val email: String,

    val emailHash: String,

    val name: String,

    val photoUrl: String? = null,

    val description: String? = null,

    val publicKey: String,

    val fcmToken: String? = null,

    val emailVerified: Boolean,

    val authProvider: String,

    val locale: String? = null,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: Date = Date(),

    @UpdateTimestamp
    val updatedAt: Date = Date(),
)
