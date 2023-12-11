package com.adarsh.connects_you_server.models.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity(name = "threads")
data class Thread(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID,

    @ManyToOne(targetEntity = Room::class)
    var room: Room,

    @ManyToOne(targetEntity = Message::class)
    var message: Message,

    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: Date,

    @UpdateTimestamp
    var updatedAt: Date
) {
    constructor() : this(UUID.randomUUID(), Room(), Message(), Date(), Date())
}