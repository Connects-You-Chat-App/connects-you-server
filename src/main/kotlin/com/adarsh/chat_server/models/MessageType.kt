package com.adarsh.chat_server.models

enum class MessageType(val type: String) {
    CHAT("CHAT"),
    JOIN("JOIN"),
    ONLINE_STATUS("ONLINE_STATUS"),
    LEAVE("LEAVE");
}