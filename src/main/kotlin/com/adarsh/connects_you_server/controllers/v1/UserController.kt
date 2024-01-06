package com.adarsh.connects_you_server.controllers.v1

import com.adarsh.connects_you_server.annotations.AuthRequired
import com.adarsh.connects_you_server.models.common.UserJWTClaim
import com.adarsh.connects_you_server.models.responses.UserResponse
import com.adarsh.connects_you_server.models.responses.UsersResponse
import com.adarsh.connects_you_server.services.v1.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/user")
class UserController(
    private val userService: UserService
) {
    @AuthRequired
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserResponse> {
        val user = userService.getUserById(UUID.fromString(id))
        return if (user.isPresent) ResponseEntity.ok(UserResponse(user.get())) else ResponseEntity.notFound().build()
    }

    @AuthRequired
    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserResponse> {
        val user = userService.getUserByEmail(email)
        return if (user.isPresent) ResponseEntity.ok(UserResponse(user.get())) else ResponseEntity.notFound().build()
    }

    @AuthRequired
    @GetMapping("/all")
    fun getAllUsersExcept(@RequestAttribute("user") user: UserJWTClaim): ResponseEntity<UsersResponse> {
        val users = userService.getAllUsersExcept(UUID.fromString(user.id))
        return if (users.isPresent) ResponseEntity.ok(
            UsersResponse(
                users.get().map { it.toSendableObject() },
            )
        ) else ResponseEntity.notFound().build()
    }
}