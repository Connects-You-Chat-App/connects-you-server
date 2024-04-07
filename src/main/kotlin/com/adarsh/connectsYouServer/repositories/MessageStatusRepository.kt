package com.adarsh.connectsYouServer.repositories

import com.adarsh.connectsYouServer.models.entities.MessageStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MessageStatusRepository : JpaRepository<MessageStatus, UUID> {
    fun findAllByMessageIdInAndUserId(
        messageIds: List<UUID>,
        userId: UUID,
    ): List<MessageStatus>
}