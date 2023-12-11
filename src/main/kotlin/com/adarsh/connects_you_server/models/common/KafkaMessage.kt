package com.adarsh.connects_you_server.models.common

import io.confluent.ksql.api.client.KsqlObject
import io.vertx.core.json.JsonObject
import java.text.SimpleDateFormat
import java.util.*

data class KafkaMessage(
    var id: UUID,
    var room_id: UUID,
    var sender_user_id: UUID,
    var receiver_user_id: UUID,
    var message: String,
    var type: String,
    var send_at: Date,
    var belongs_to_thread_id: UUID?,
    var belongs_to_message_id: UUID?,
) {

    constructor() : this(
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        "",
        "",
        Date(),
        null,
        null
    )

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