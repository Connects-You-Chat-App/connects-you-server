package com.adarsh.connects_you_server.controllers.v1

import com.adarsh.connects_you_server.annotations.AuthRequired
import com.adarsh.connects_you_server.models.common.UserJWTClaim
import com.adarsh.connects_you_server.models.requests.SaveSharedKeyRequest
import com.adarsh.connects_you_server.models.responses.GetUserSharedKeysResponse
import com.adarsh.connects_you_server.services.v1.SharedKeyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/shared-key")
class SharedKeyController(private val sharedKeyService: SharedKeyService) {
    @AuthRequired
    @GetMapping()
    private fun getKeys(@RequestAttribute("user") user: UserJWTClaim): ResponseEntity<GetUserSharedKeysResponse> {
        val response = sharedKeyService.getAllSharedKeys(UUID.fromString(user.id))
        return ResponseEntity.ok(response)
    }

    @AuthRequired
    @PostMapping("/save-key")
    private fun saveKey(
        @RequestBody saveSharedKey: SaveSharedKeyRequest, @RequestAttribute("user") user: UserJWTClaim
    ): ResponseEntity<Unit> {
        sharedKeyService.saveUserKeys(UUID.fromString(user.id), listOf(saveSharedKey))
        return ResponseEntity.ok().build()
    }

    @AuthRequired
    @PostMapping("/save-keys")
    private fun saveKeys(
        @RequestBody saveSharedKeys: List<SaveSharedKeyRequest>, @RequestAttribute("user") user: UserJWTClaim
    ): ResponseEntity<Unit> {
        sharedKeyService.saveUserKeys(UUID.fromString(user.id), saveSharedKeys)
        return ResponseEntity.ok().build()
    }
}