package com.adarsh.connectsYouServer.models.entities

import com.adarsh.connectsYouServer.models.enums.RoomTypeEnum
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.Date
import java.util.UUID

@Entity(name = "rooms")
data class Room(
    @Id
    var id: UUID = UUID.randomUUID(),
    var name: String? = null,
    var logoUrl: String? = null,
    var description: String? = null,
    var isDeleted: Boolean = false,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: RoomTypeEnum,
    @ManyToOne(targetEntity = User::class)
    @JoinColumn(nullable = false)
    var creatorUser: User,
    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: Date = Date(),
    @UpdateTimestamp
    var updatedAt: Date = Date(),
) {
    constructor() : this(UUID.randomUUID(), "", "", "", false, RoomTypeEnum.DUET, User())
}