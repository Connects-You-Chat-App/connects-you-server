package com.adarsh.connects_you_server.controllers.v1

import com.adarsh.connects_you_server.annotations.AuthRequired
import com.adarsh.connects_you_server.models.common.UserJWTClaim
import com.adarsh.connects_you_server.models.requests.AuthRequest
import com.adarsh.connects_you_server.models.requests.SaveUserKeysRequest
import com.adarsh.connects_you_server.models.responses.AuthResponse
import com.adarsh.connects_you_server.services.v1.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/auth")
class AuthController(private val authService: AuthService) {
    @PostMapping("/authenticate")
    private fun authenticate(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        val response = authService.authenticate(authRequest)
        return ResponseEntity.ok(response)
    }

    @AuthRequired(allowExpired = true)
    @PatchMapping("/sign-out")
    private fun signOut(@RequestAttribute("user") user: UserJWTClaim): ResponseEntity<Unit> {
        val response = authService.signOut(user)
        return ResponseEntity.ok().build()
    }

    @AuthRequired(allowExpired = true)
    @PatchMapping("/refresh-token")
    private fun refreshToken(@RequestAttribute("user") user: UserJWTClaim): ResponseEntity<Unit> {
        val response = authService.refreshToken(user)
        return ResponseEntity.ok().build()
    }

    @AuthRequired
    @PostMapping("/keys")
    private fun saveUserKeys(
        @RequestBody saveUserKeysRequest: SaveUserKeysRequest, @RequestAttribute("user") user: UserJWTClaim
    ): ResponseEntity<Unit> {
        authService.saveUserKeys(UUID.fromString(user.id), saveUserKeysRequest)
        return ResponseEntity.ok().build()
    }
}