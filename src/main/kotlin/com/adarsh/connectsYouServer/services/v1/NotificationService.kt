package com.adarsh.connectsYouServer.services.v1

import com.adarsh.connectsYouServer.models.entities.GroupInvitation
import com.adarsh.connectsYouServer.repositories.GroupInvitationRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationService(
    private val groupInvitationRepository: GroupInvitationRepository,
) {
    fun fetchAllUsersNotifications(userId: UUID) = groupInvitationRepository.findAllByReceiverUserId(userId)

    fun saveAll(groupInvitations: List<GroupInvitation>) = groupInvitationRepository.saveAll(groupInvitations)

    fun fetchByInvitationId(id: UUID) = groupInvitationRepository.findById(id)

    fun deleteByInvitationId(id: UUID) = groupInvitationRepository.deleteById(id)
}
