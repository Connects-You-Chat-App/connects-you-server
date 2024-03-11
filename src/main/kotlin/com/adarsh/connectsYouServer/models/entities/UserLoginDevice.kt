package com.adarsh.connectsYouServer.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.Date
import java.util.UUID

@Entity(name = "user_login_devices")
data class UserLoginDevice(
    @Id
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
