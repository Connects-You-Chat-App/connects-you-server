package com.adarsh.connectsYouServer.controllers.v1

import com.adarsh.connectsYouServer.annotations.AuthRequired
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.responses.UserResponse
import com.adarsh.connectsYouServer.models.responses.UsersResponse
import com.adarsh.connectsYouServer.services.v1.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/user")
class UserController(
    private val userService: UserService,
) {
    @AuthRequired
    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: String,
    ): ResponseEntity<UserResponse> {
        val user = userService.fetchUserById(UUID.fromString(id))
        return if (user.isPresent) ResponseEntity.ok(UserResponse(user.get())) else ResponseEntity.notFound().build()
    }

    @AuthRequired
    @GetMapping("/email/{email}")
    fun getUserByEmail(
        @PathVariable email: String,
    ): ResponseEntity<UserResponse> {
        val user = userService.fetchUserByEmail(email)
        return if (user.isPresent) ResponseEntity.ok(UserResponse(user.get())) else ResponseEntity.notFound().build()
    }

    @AuthRequired
    @GetMapping("/all")
    fun getAllUsersExcept(
        @RequestAttribute("user") user: UserJWTClaim,
    ): ResponseEntity<UsersResponse> {
        val users = userService.fetchAllUsersExcept(UUID.fromString(user.id))
        return ResponseEntity.ok(
            UsersResponse(
                users.map { it.toSendableObject() },
            ),
        )
    }
}