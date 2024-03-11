package com.adarsh.connectsYouServer.controllers.v1

import com.adarsh.connectsYouServer.annotations.AuthRequired
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.requests.AuthRequest
import com.adarsh.connectsYouServer.models.requests.SaveUserKeysRequest
import com.adarsh.connectsYouServer.models.responses.AuthResponse
import com.adarsh.connectsYouServer.services.v1.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/auth")
class AuthController(private val authService: AuthService) {
    @PostMapping("/authenticate")
    private fun authenticate(
        @RequestBody authRequest: AuthRequest,
    ): ResponseEntity<AuthResponse> {
        val response = authService.authenticate(authRequest)
        return ResponseEntity.ok(response)
    }

    @AuthRequired(allowExpired = true)
    @PatchMapping("/sign-out")
    private fun signOut(
        @RequestAttribute("user") user: UserJWTClaim,
    ): ResponseEntity<Unit> {
        val response = authService.signOut(user)
        return ResponseEntity.ok().build()
    }

    @AuthRequired(allowExpired = true)
    @PatchMapping("/refresh-token")
    private fun refreshToken(
        @RequestAttribute("user") user: UserJWTClaim,
    ): ResponseEntity<Unit> {
        val response = authService.refreshToken(user)
        return ResponseEntity.ok().build()
    }

    @AuthRequired
    @PostMapping("/keys")
    private fun saveUserKeys(
        @RequestBody saveUserKeysRequest: SaveUserKeysRequest,
        @RequestAttribute("user") user: UserJWTClaim,
    ): ResponseEntity<Unit> {
        authService.saveUserKeys(UUID.fromString(user.id), saveUserKeysRequest)
        return ResponseEntity.ok().build()
    }
}
