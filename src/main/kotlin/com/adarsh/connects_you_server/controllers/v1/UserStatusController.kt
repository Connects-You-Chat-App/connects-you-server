package com.adarsh.connects_you_server.controllers.v1

import com.adarsh.connects_you_server.annotations.AuthRequired
import com.adarsh.connects_you_server.models.common.UserJWTClaim
import com.adarsh.connects_you_server.models.entities.UserStatus
import com.adarsh.connects_you_server.models.requests.UserStatusRequest
import com.adarsh.connects_you_server.services.v1.UserStatusService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@AuthRequired
@RequestMapping("${Constants.VERSION_PREFIX}/user-status")
class UserStatusController(
    private val userStatusService: UserStatusService
) {
    @AuthRequired
    @PostMapping("/")
    fun createUserStatus(
        @RequestAttribute("user") user: UserJWTClaim,
        @RequestBody() userStatusRequest: UserStatusRequest
    ): ResponseEntity<UserStatus?> {
        return ResponseEntity.ok(userStatusService.createUserStatus(UUID.fromString(user.id), userStatusRequest))
    }

    @AuthRequired
    @PutMapping("/")
    fun updateUserStatus(
        @RequestAttribute("user") user: UserJWTClaim,
        @RequestBody() userStatusRequest: UserStatusRequest
    ): ResponseEntity<Unit> {
        userStatusService.updateUserStatus(UUID.fromString(user.id), userStatusRequest)
        return ResponseEntity.ok().build()
    }

    @AuthRequired
    @DeleteMapping("/")
    fun deleteUserStatus(
        @RequestAttribute("user") user: UserJWTClaim,
    ): ResponseEntity<Unit> {
        userStatusService.deleteUserStatus(UUID.fromString(user.id))
        return ResponseEntity.ok().build()
    }

    @AuthRequired
    @GetMapping("/{userId}")
    fun getUserStatus(@PathVariable userId: String): ResponseEntity<UserStatus> {
        val userStatus = userStatusService.getUserStatus(UUID.fromString(userId))
        return if (userStatus.isPresent) ResponseEntity.ok(userStatus.get()) else ResponseEntity.notFound().build()
    }

    @AuthRequired
    @GetMapping("/")
    fun getMyUserStatus(@RequestAttribute("user") user: UserJWTClaim): ResponseEntity<UserStatus> {
        val userStatus = userStatusService.getUserStatus(UUID.fromString(user.id))
        return if (userStatus.isPresent) ResponseEntity.ok(userStatus.get()) else ResponseEntity.notFound().build()
    }
}