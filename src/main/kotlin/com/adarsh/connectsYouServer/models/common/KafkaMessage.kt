package com.adarsh.connectsYouServer.models.common

import com.adarsh.connectsYouServer.models.enums.KafkaMessageType
import com.adarsh.connectsYouServer.models.enums.SocketEventType
import com.adarsh.connectsYouServer.utils.JSON
import java.io.Serializable

data class KafkaMessage(
    var eventType: SocketEventType,
    var kafkaMessageType: KafkaMessageType = KafkaMessageType.GENERAL,
    var data: Map<*, *>? = null,
) : Serializable {
    constructor() : this(SocketEventType.USER_PRESENCE, KafkaMessageType.GENERAL, null)

    override fun toString(): String {
        return JSON.toJson(this)
    }
}