package com.adarsh.connects_you_server.models.common

import com.adarsh.connects_you_server.models.enums.KafkaMessageType
import com.adarsh.connects_you_server.models.enums.SocketEventType
import com.adarsh.connects_you_server.utils.JSON
import java.io.Serializable

data class KafkaMessage(
    var eventType: SocketEventType,
    var kafkaMessageType: KafkaMessageType = KafkaMessageType.GENERAL,
    var data: Map<*, *>? = null
) : Serializable {
    constructor() : this(SocketEventType.USER_PRESENCE, KafkaMessageType.GENERAL, null)

    override fun toString(): String {
        return JSON.toJson(this)
    }
}