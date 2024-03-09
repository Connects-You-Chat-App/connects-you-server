package com.adarsh.connectsYouServer.repositories

import com.adarsh.connectsYouServer.models.entities.GroupInvitation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface GroupInvitationRepository : JpaRepository<GroupInvitation, UUID> {
    fun findAllByReceiverUserId(userId: UUID): List<GroupInvitation>
}