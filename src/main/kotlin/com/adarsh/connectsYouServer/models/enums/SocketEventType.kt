package com.adarsh.connectsYouServer.models.enums

enum class SocketEventType {
    USER_PRESENCE,
    USER_OFFLINE,
    USER_TYPING,
    DUET_ROOM_CREATED,
    GROUP_INVITATION,
    GROUP_JOINED,
    ROOM_MESSAGE,
    ROOM_MESSAGE_EDIT,
    USER_STATUS,
    ROOM_MESSAGE_DELIVERED,
    ROOM_MESSAGE_READ,
    ;

    companion object {
        fun fromString(type: String): SocketEventType {
            for (value in entries) {
                if (value.name == type) {
                    return value
                }
            }
            throw IllegalArgumentException("Invalid socket event type $type")
        }
    }
}
