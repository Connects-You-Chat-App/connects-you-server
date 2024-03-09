package com.adarsh.connectsYouServer.controllers.v1

import com.adarsh.connectsYouServer.annotations.AuthRequired
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.entities.UserStatus
import com.adarsh.connectsYouServer.models.requests.UserStatusRequest
import com.adarsh.connectsYouServer.services.v1.UserStatusService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@AuthRequired
@RequestMapping("${Constants.VERSION_PREFIX}/user-status")
class UserStatusController(
    private val userStatusService: UserStatusService,
) {
    @AuthRequired
    @PostMapping("/")
    fun createUserStatus(
        @RequestAttribute("user") user: UserJWTClaim,
        @RequestBody() userStatusRequest: UserStatusRequest,
    ): ResponseEntity<UserStatus?> {
        return ResponseEntity.ok(userStatusService.createUserStatus(UUID.fromString(user.id), userStatusRequest))
    }

    @AuthRequired
    @PutMapping("/")
    fun updateUserStatus(
        @RequestAttribute("user") user: UserJWTClaim,
        @RequestBody() userStatusRequest: UserStatusRequest,
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
    fun getUserStatus(
        @PathVariable userId: String,
    ): ResponseEntity<UserStatus> {
        val userStatus = userStatusService.getUserStatus(UUID.fromString(userId))
        return if (userStatus.isPresent) ResponseEntity.ok(userStatus.get()) else ResponseEntity.notFound().build()
    }

    @AuthRequired
    @GetMapping("/")
    fun getMyUserStatus(
        @RequestAttribute("user") user: UserJWTClaim,
    ): ResponseEntity<UserStatus> {
        val userStatus = userStatusService.getUserStatus(UUID.fromString(user.id))
        return if (userStatus.isPresent) ResponseEntity.ok(userStatus.get()) else ResponseEntity.notFound().build()
    }
}