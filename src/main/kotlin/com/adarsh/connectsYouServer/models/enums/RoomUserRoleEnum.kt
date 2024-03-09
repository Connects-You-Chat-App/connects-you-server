package com.adarsh.connectsYouServer.models.enums

enum class RoomUserRoleEnum {
    DUET_CREATOR,
    DUET_PARTICIPANT,
    GROUP_ADMIN,
    GROUP_MEMBER,
    ;

    companion object {
        fun fromString(type: String): RoomUserRoleEnum {
            for (value in entries) {
                if (value.name == type) {
                    return value
                }
            }
            throw IllegalArgumentException("Invalid room user role $type")
        }
    }
}