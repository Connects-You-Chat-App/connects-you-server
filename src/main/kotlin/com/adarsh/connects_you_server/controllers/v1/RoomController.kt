package com.adarsh.connects_you_server.controllers.v1

import com.adarsh.connects_you_server.annotations.AuthRequired
import com.adarsh.connects_you_server.models.common.UserJWTClaim
import com.adarsh.connects_you_server.models.requests.CreateDuetRoomRequest
import com.adarsh.connects_you_server.models.requests.CreateGroupRoomRequest
import com.adarsh.connects_you_server.models.requests.JoinGroupRequest
import com.adarsh.connects_you_server.models.responses.GetAllRoomsWithRoomUsersResponse
import com.adarsh.connects_you_server.models.responses.RoomWithRoomUsersResponse
import com.adarsh.connects_you_server.services.v1.RoomService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/room")
class RoomController(
    private val roomService: RoomService
) {
    @AuthRequired
    @PostMapping("/create-duet")
    fun createDuetRoom(
        @RequestBody createDuetRoomRequest: CreateDuetRoomRequest,
        @RequestAttribute("user") user: UserJWTClaim
    ): ResponseEntity<RoomWithRoomUsersResponse> {
        return ResponseEntity.ok(RoomWithRoomUsersResponse(roomService.createDuetRoom(user, createDuetRoomRequest)))
    }

    @AuthRequired
    @PostMapping("/create-group")
    fun createGroupRoom(
        @RequestBody createGroupRoomRequest: CreateGroupRoomRequest,
        @RequestAttribute("user") user: UserJWTClaim
    ): ResponseEntity<RoomWithRoomUsersResponse> {
        return ResponseEntity.ok(RoomWithRoomUsersResponse(roomService.createGroupRoom(user, createGroupRoomRequest)))
    }

    @AuthRequired
    @PostMapping("/join-group")
    fun joinGroup(
        @RequestBody joinGroupRequest: JoinGroupRequest,
        @RequestAttribute("user") user: UserJWTClaim
    ): ResponseEntity<RoomWithRoomUsersResponse> {
        return ResponseEntity.ok(RoomWithRoomUsersResponse(roomService.joinGroup(user, joinGroupRequest)))
    }

    @AuthRequired
    @GetMapping("/all")
    fun getRooms(@RequestAttribute("user") user: UserJWTClaim): ResponseEntity<GetAllRoomsWithRoomUsersResponse> {
        return ResponseEntity.ok(GetAllRoomsWithRoomUsersResponse(roomService.fetchRooms(user)))
    }
}