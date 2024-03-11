package com.adarsh.connectsYouServer.models.responses

import com.adarsh.connectsYouServer.models.common.RoomWithRoomUsers

data class GetAllRoomsWithRoomUsersResponse(
    val rooms: List<RoomWithRoomUsers>,
)
