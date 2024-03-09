package com.adarsh.connectsYouServer.repositories

import com.adarsh.connectsYouServer.models.entities.User
import com.adarsh.connectsYouServer.models.entities.UserSharedKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Date
import java.util.UUID

@Repository
interface UserSharedKeyRepository : JpaRepository<UserSharedKey, UUID> {
    @Query("SELECT u FROM UserSharedKey u WHERE u.creatorUser.id = :creatorUserId")
    fun findAllByCreatorUserId(creatorUserId: UUID): List<UserSharedKey>

    fun findAllByCreatorUserAndUpdatedAtGreaterThan(
        creatorUser: User,
        updatedAt: Date,
    ): List<UserSharedKey>
}