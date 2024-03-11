package com.adarsh.connectsYouServer.controllers.v1

import com.adarsh.connectsYouServer.annotations.AuthRequired
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.requests.SaveSharedKeyRequest
import com.adarsh.connectsYouServer.models.responses.GetUserSharedKeysResponse
import com.adarsh.connectsYouServer.services.v1.SharedKeyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/shared-key")
class SharedKeyController(private val sharedKeyService: SharedKeyService) {
    @AuthRequired
    @GetMapping()
    private fun getKeys(
        @RequestAttribute("user") user: UserJWTClaim,
    ): ResponseEntity<GetUserSharedKeysResponse> {
        return ResponseEntity.ok(
            GetUserSharedKeysResponse.fromListOfUserSharedKey(
                sharedKeyService.fetchSharedKeysByUserId(
                    UUID.fromString(user.id),
                ),
            ),
        )
    }

    @AuthRequired
    @PostMapping("/save-key")
    private fun saveKey(
        @RequestBody saveSharedKey: SaveSharedKeyRequest,
        @RequestAttribute("user") user: UserJWTClaim,
    ): ResponseEntity<Unit> {
        sharedKeyService.saveUserKeys(UUID.fromString(user.id), listOf(saveSharedKey))
        return ResponseEntity.ok().build()
    }

    @AuthRequired
    @PostMapping("/save-keys")
    private fun saveKeys(
        @RequestBody saveSharedKeys: List<SaveSharedKeyRequest>,
        @RequestAttribute("user") user: UserJWTClaim,
    ): ResponseEntity<Unit> {
        sharedKeyService.saveUserKeys(UUID.fromString(user.id), saveSharedKeys)
        return ResponseEntity.ok().build()
    }
}
