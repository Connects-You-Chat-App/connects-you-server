package com.adarsh.connectsYouServer.models.enums

enum class MessageTypeEnum {
    TEXT,
    IMAGE,
    VIDEO,
    DOC,
    PPT,
    PDF,
    TXT,
    XLS,
    OTHER,
    ;

    companion object {
        fun fromString(type: String): MessageTypeEnum {
            for (value in entries) {
                if (value.name == type) {
                    return value
                }
            }
            throw IllegalArgumentException("Invalid message type $type")
        }
    }
}
