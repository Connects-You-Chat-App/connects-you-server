package com.adarsh.connectsYouServer.controllers.v1

import com.adarsh.connectsYouServer.annotations.AuthRequired
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.requests.EditMessageRequest
import com.adarsh.connectsYouServer.models.requests.SendMessageRequest
import com.adarsh.connectsYouServer.models.responses.MessageResponse
import com.adarsh.connectsYouServer.models.responses.RoomMessagesResponse
import com.adarsh.connectsYouServer.services.v1.MessageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/message")
class MessageController(
    private val messageService: MessageService,
) {
    @AuthRequired
    @PostMapping("/send")
    fun sendMessage(
        @RequestAttribute("user") user: UserJWTClaim,
        @RequestBody sendMessageRequest: SendMessageRequest,
    ): ResponseEntity<Unit> {
        messageService.sendMessage(user, sendMessageRequest)
        return ResponseEntity.ok().build()
    }

    @AuthRequired
    @PostMapping("/edit")
    fun editMessage(
        @RequestAttribute("user") user: UserJWTClaim,
        @RequestBody editMessageRequest: EditMessageRequest,
    ): ResponseEntity<Unit> {
        messageService.editMessage(user, editMessageRequest)
        return ResponseEntity.ok().build()
    }

    @AuthRequired
    @GetMapping("/by-room-id/{roomId}")
    fun fetchMessagesByRoomId(
        @RequestAttribute("user") user: UserJWTClaim,
        @PathVariable("roomId") roomId: String,
    ): ResponseEntity<RoomMessagesResponse> {
        return ResponseEntity.ok(
            RoomMessagesResponse(
                roomId,
                messageService.fetchMessagesByRoomId(UUID.fromString(roomId)).map { MessageResponse.fromMessage(it) },
            ),
        )
    }
}
