package com.adarsh.connects_you_server.models.entities

import com.adarsh.connects_you_server.models.enums.MessageTypeEnum
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity(name = "messages")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID,

    @ManyToOne(targetEntity = Room::class)
    var room: Room,

    @ManyToOne(targetEntity = User::class)
    var senderUser: User,

    @ManyToOne(targetEntity = User::class)
    var receiverUser: User,

    var message: String,

    @Enumerated(EnumType.STRING)
    var type: MessageTypeEnum,

    @ManyToOne(targetEntity = Thread::class)
    var belongsToThread: Thread? = null,

    @ManyToOne(targetEntity = Message::class)
    var belongsToMessage: Message? = null,

    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: Date = Date(),

    @UpdateTimestamp
    var updatedAt: Date = Date(),
) {
    constructor() : this(UUID.randomUUID(), Room(), User(), User(), "", MessageTypeEnum.TEXT)
}