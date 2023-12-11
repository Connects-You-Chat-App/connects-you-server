package com.adarsh.connects_you_server.repositories

import com.adarsh.connects_you_server.models.entities.UserLoginDevice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserLoginDeviceRepository : JpaRepository<UserLoginDevice, UUID> {
    @Modifying
    @Transactional
    @Query("UPDATE user_login_devices ulh SET ulh.fcmToken = :fcmToken, ulh.isActive = true WHERE ulh.user.id = :userId AND ulh.deviceId = :deviceId")
    fun updateFcmTokenByUserIdAndDeviceId(
        @Param("userId") userId: UUID,
        @Param("deviceId") deviceId: String,
        @Param("fcmToken") fcmToken: String
    )

    @Modifying
    @Transactional
    @Query("UPDATE user_login_devices ulh SET ulh.isActive = :isActive WHERE ulh.user.id = :userId AND ulh.deviceId = :deviceId")
    fun updateIsActiveByUserIdAndDeviceId(
        @Param("userId") userId: UUID,
        @Param("deviceId") deviceId: String,
        @Param("isActive") isActive: Boolean
    )
}