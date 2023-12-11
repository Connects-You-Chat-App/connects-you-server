package com.adarsh.connects_you_server.models.entities

import com.adarsh.connects_you_server.models.enums.RoomTypeEnum
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity(name = "rooms")
data class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID,

    var name: String? = null,

    var logoUrl: String? = null,

    var description: String? = null,

    @Enumerated(EnumType.STRING)
    var type: RoomTypeEnum,

    @ManyToOne(targetEntity = User::class)
    var creatorUser: User,

    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: Date = Date(),

    @UpdateTimestamp
    var updatedAt: Date = Date(),
) {
    constructor() : this(UUID.randomUUID(), "", "", "", RoomTypeEnum.DUET, User())
}