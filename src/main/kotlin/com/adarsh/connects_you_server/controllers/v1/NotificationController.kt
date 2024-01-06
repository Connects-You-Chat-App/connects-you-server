package com.adarsh.connects_you_server.controllers.v1

import com.adarsh.connects_you_server.annotations.AuthRequired
import com.adarsh.connects_you_server.models.common.UserJWTClaim
import com.adarsh.connects_you_server.models.responses.GetAllNotificationsResponse
import com.adarsh.connects_you_server.models.responses.NotificationResponse
import com.adarsh.connects_you_server.services.v1.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/notification")
class NotificationController(
    private val notificationService: NotificationService
) {
    @AuthRequired
    @GetMapping("/all")
    fun getAllUsersNotifications(@RequestAttribute("user") user: UserJWTClaim): ResponseEntity<GetAllNotificationsResponse> {
        return ResponseEntity.ok(
            GetAllNotificationsResponse(
                notificationService.getAllUsersNotifications(UUID.fromString(user.id)).map {
                    NotificationResponse.fromGroupNotification(it)
                }
            )
        )
    }
}