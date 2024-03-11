package com.adarsh.connectsYouServer.controllers.v1

import com.adarsh.connectsYouServer.annotations.AuthRequired
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.responses.GetAllNotificationsResponse
import com.adarsh.connectsYouServer.models.responses.NotificationResponse
import com.adarsh.connectsYouServer.services.v1.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("${Constants.VERSION_PREFIX}/notification")
class NotificationController(
    private val notificationService: NotificationService,
) {
    @AuthRequired
    @GetMapping("/all")
    fun getAllUsersNotifications(
        @RequestAttribute("user") user: UserJWTClaim,
    ): ResponseEntity<GetAllNotificationsResponse> {
        return ResponseEntity.ok(
            GetAllNotificationsResponse(
                notificationService.fetchAllUsersNotifications(UUID.fromString(user.id)).map {
                    NotificationResponse.fromGroupNotification(it)
                },
            ),
        )
    }
}
