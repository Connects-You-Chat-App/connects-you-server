package com.adarsh.connects_you_server.repositories

import com.adarsh.connects_you_server.models.entities.GroupInvitation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupInvitationRepository : JpaRepository<GroupInvitation, UUID> {
    fun findAllByReceiverUserId(userId: UUID): List<GroupInvitation>
}