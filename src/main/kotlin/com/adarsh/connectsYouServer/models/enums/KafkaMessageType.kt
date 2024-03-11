package com.adarsh.connectsYouServer.models.enums

enum class KafkaMessageType {
    GENERAL,
    GROUP_INVITATION,
    GROUP_JOINED,
    DUET_ROOM_CREATED_MESSAGE,
    ;

    companion object {
        fun fromString(type: String): KafkaMessageType {
            for (value in entries) {
                if (value.name == type) {
                    return value
                }
            }
            throw IllegalArgumentException("Invalid kafka event type $type")
        }
    }
}
