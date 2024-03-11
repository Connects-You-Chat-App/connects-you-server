package com.adarsh.connectsYouServer.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.Date
import java.util.UUID

@Entity(name = "user_status")
data class UserStatus(
    @Id
    var id: UUID = UUID.randomUUID(),
    @OneToOne(targetEntity = User::class)
    var user: User,
    var status: String,
    var validTill: Date,
    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: Date = Date(),
    @UpdateTimestamp
    var updatedAt: Date = Date(),
) {
    constructor() : this(
        UUID.randomUUID(),
        User(),
        "",
        Date(),
    )
}
