package com.adarsh.connects_you_server.services.v1

import com.adarsh.connects_you_server.models.entities.GroupInvitation
import com.adarsh.connects_you_server.repositories.GroupInvitationRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class NotificationService(
    private val groupInvitationRepository: GroupInvitationRepository
) {
    fun getAllUsersNotifications(userId: UUID): List<GroupInvitation> {
        return groupInvitationRepository.findAllByReceiverUserId(userId)
    }
}