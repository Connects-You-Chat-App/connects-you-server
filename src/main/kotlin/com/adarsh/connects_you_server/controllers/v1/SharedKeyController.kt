package com.adarsh.connects_you_server.controllers.v1

import com.adarsh.connects_you_server.annotations.IsAuthRequired
import com.adarsh.connects_you_server.models.common.UserJWTClaim
import com.adarsh.connects_you_server.models.entities.UserSharedKey
import com.adarsh.connects_you_server.models.requests.SaveSharedKeyRequest
import com.adarsh.connects_you_server.services.v1.SharedKeyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/shared-key")
class SharedKeyController(private val sharedKeyService: SharedKeyService) {
    @IsAuthRequired
    @GetMapping("/get")
    fun getKeys(@RequestAttribute("user") user: UserJWTClaim): ResponseEntity<List<UserSharedKey>> {
        val response = sharedKeyService.getAllSharedKeys(UUID.fromString(user.id))
        return ResponseEntity.ok(response)
    }

    @IsAuthRequired
    @PostMapping("/save-key")
    fun saveKey(
        @RequestBody saveSharedKey: SaveSharedKeyRequest, @RequestAttribute("user") user: UserJWTClaim
    ): ResponseEntity<Unit> {
        sharedKeyService.saveUserKeys(UUID.fromString(user.id), listOf(saveSharedKey))
        return ResponseEntity.ok().build()
    }

    @IsAuthRequired
    @PostMapping("/save-keys")
    fun saveKeys(
        @RequestBody saveSharedKeys: List<SaveSharedKeyRequest>, @RequestAttribute("user") user: UserJWTClaim
    ): ResponseEntity<Unit> {
        sharedKeyService.saveUserKeys(UUID.fromString(user.id), saveSharedKeys)
        return ResponseEntity.ok().build()
    }
}