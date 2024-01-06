package com.adarsh.connects_you_server.models.enums

enum class RoomTypeEnum {
    DUET,
    GROUP;

    companion object {
        fun fromString(type: String): RoomTypeEnum {
            for (value in entries) {
                if (value.name == type) {
                    return value
                }
            }
            throw IllegalArgumentException("Invalid room type $type")
        }
    }
}