package com.adarsh.connects_you_server.repositories

import com.adarsh.connects_you_server.models.entities.UserStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserStatusRepository : JpaRepository<UserStatus, UUID> {
    fun findByUserId(userId: UUID): Optional<UserStatus>

    @Modifying
    @Transactional
    @Query("UPDATE user_status SET status = :status, validTill = :validTill WHERE user.id = :userId")
    fun updateByUserId(userId: UUID, status: String, validTill: Date)

    @Modifying
    @Transactional
    @Query("DELETE FROM user_status WHERE user.id = :userId")
    fun deleteByUserId(userId: UUID)
}