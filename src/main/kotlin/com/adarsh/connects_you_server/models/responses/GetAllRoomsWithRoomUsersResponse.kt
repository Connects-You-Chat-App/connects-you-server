package com.adarsh.connects_you_server.models.responses

import com.adarsh.connects_you_server.models.common.RoomWithRoomUsers

data class GetAllRoomsWithRoomUsersResponse(
    val rooms: List<RoomWithRoomUsers>
)