package com.adarsh.connects_you_server.models.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
@Table(name = "user_shared_keys")
data class UserSharedKey(
    @Id
    var id: UUID = UUID.randomUUID(),

    @ManyToOne(targetEntity = User::class)
    var creatorUser: User,

    @ManyToOne(targetEntity = User::class)
    var forUser: User?,

    @ManyToOne(targetEntity = Room::class)
    var forRoom: Room?,

    var key: String,

    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: Date = Date(),

    @UpdateTimestamp
    var updatedAt: Date = Date(),
) {
    constructor() : this(UUID.randomUUID(), User(), null, null, "", Date(), Date())
}