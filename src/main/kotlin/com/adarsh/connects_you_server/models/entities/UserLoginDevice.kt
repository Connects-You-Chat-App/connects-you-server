package com.adarsh.connects_you_server.models.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity(name = "user_login_devices")
data class UserLoginDevice(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),

    @Column(unique = true)
    var fcmToken: String,

    @ManyToOne(targetEntity = User::class)
    var user: User,

    @Column(updatable = false)
    var deviceId: String,

    var deviceName: String,

    @Column(name = "device_os")
    var deviceOS: String,

    var isActive: Boolean,

    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: Date = Date(),

    @UpdateTimestamp
    var updatedAt: Date = Date(),
) {
    constructor() : this(
        UUID.randomUUID(),
        "",
        User(),
        "",
        "",
        "",
        false,
    )
}