package com.adarsh.chat_server.models

import io.confluent.ksql.api.client.KsqlObject
import io.vertx.core.json.JsonObject
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.text.SimpleDateFormat
import java.util.*

@Entity(name = "messages")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    @ManyToOne(targetEntity = Room::class)
    val room: Room,

    @ManyToOne(targetEntity = User::class)
    val senderUser: User,

    @ManyToOne(targetEntity = User::class)
    val receiverUser: User,

    val message: String,

    @Enumerated(EnumType.STRING)
    val type: MessageTypeEnum,

    @ManyToOne(targetEntity = Thread::class)
    val belongsToThread: Thread? = null,

    @ManyToOne(targetEntity = Message::class)
    val belongsToMessage: Message? = null,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: Date = Date(),

    @UpdateTimestamp
    val updatedAt: Date = Date(),
)

data class KafkaMessage(
    val id: UUID,
    val room_id: UUID,
    val sender_user_id: UUID,
    val receiver_user_id: UUID,
    val message: String,
    val type: String,
    val send_at: Date,
    val belongs_to_thread_id: UUID?,
    val belongs_to_message_id: UUID?,
) {
    companion object {
        private fun getStringOrNull(data: JsonObject, key: String): String? {
            return if (data.containsKey(key)) {
                data.getString(key)
            } else {
                null
            }
        }

        fun fromKafkaResponse(json: String): KafkaMessage {
            val data = JsonObject(json)

            val belongsToThreadId = getStringOrNull(data, "BELONGS_TO_THREAD_ID")
            val belongsToMessageId = getStringOrNull(data, "BELONGS_TO_MESSAGE_ID")

            return KafkaMessage(
                id = UUID.fromString(data.getString("ID")),
                room_id = UUID.fromString(data.getString("ROOM_ID")),
                sender_user_id = UUID.fromString(data.getString("SENDER_USER_ID")),
                receiver_user_id = UUID.fromString(data.getString("RECEIVER_USER_ID")),
                message = data.getString("MESSAGE"),
                type = data.getString("TYPE"),
                send_at = SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss zzz yyyy",
                    Locale.ENGLISH
                ).parse(data.getString("SEND_AT")),
                belongs_to_thread_id = if (belongsToThreadId == null) null else UUID.fromString(belongsToThreadId),
                belongs_to_message_id = if (belongsToMessageId == null) null else UUID.fromString(
                    belongsToMessageId
                )
            )
        }

        fun toKafkaObject(message: KafkaMessage): KsqlObject {
            val data = mutableMapOf<String, String>()
            data["ID"] = message.id.toString()
            data["ROOM_ID"] = message.room_id.toString()
            data["SENDER_USER_ID"] = message.sender_user_id.toString()
            data["RECEIVER_USER_ID"] = message.receiver_user_id.toString()
            data["MESSAGE"] = message.message
            data["TYPE"] = message.type
            data["SEND_AT"] = message.send_at.toString()
            if (message.belongs_to_thread_id != null) {
                data["BELONGS_TO_THREAD_ID"] = message.belongs_to_thread_id.toString()
            }
            if (message.belongs_to_message_id != null) {
                data["BELONGS_TO_MESSAGE_ID"] = message.belongs_to_message_id.toString()
            }
            return KsqlObject(data as Map<String, Any>)
        }
    }

    fun toKafkaObject() = toKafkaObject(this)
}
