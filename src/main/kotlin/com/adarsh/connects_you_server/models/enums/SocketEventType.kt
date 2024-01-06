package com.adarsh.connects_you_server.models.enums

enum class SocketEventType {
    USER_PRESENCE,
    USER_OFFLINE,
    USER_TYPING,
    USER_STOP_TYPING,
    DUET_ROOM_CREATED,
    GROUP_INVITATION,
    GROUP_JOINED,
    USER_STATUS;

    companion object {
        fun fromString(type: String): SocketEventType {
            for (value in entries) {
                if (value.name == type) {
                    return value
                }
            }
            throw IllegalArgumentException("Invalid socket event type ${type}")
        }
    }
}