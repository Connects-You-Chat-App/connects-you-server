package com.adarsh.connects_you_server.repositories

import com.adarsh.connects_you_server.models.entities.UserLoginDevice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserLoginDeviceRepository : JpaRepository<UserLoginDevice, UUID> {
    @Modifying
    @Transactional
    @Query("UPDATE user_login_devices ulh SET ulh.fcmToken = :fcmToken, ulh.isActive = true WHERE ulh.user.id = :userId AND ulh.deviceId = :deviceId")
    fun updateFcmTokenByUserIdAndDeviceId(
        userId: UUID,
        deviceId: String,
        fcmToken: String
    )

    @Modifying
    @Transactional
    @Query("UPDATE user_login_devices ulh SET ulh.isActive = :isActive WHERE ulh.user.id = :userId AND ulh.deviceId = :deviceId")
    fun updateIsActiveByUserIdAndDeviceId(
        userId: UUID,
        deviceId: String,
        isActive: Boolean
    )
}