package com.adarsh.connectsYouServer.repositories

import com.adarsh.connectsYouServer.models.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.Optional
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): Optional<User>

    @Modifying
    @Transactional
    @Query("UPDATE users u SET u.publicKey = :publicKey, u.privateKey = :privateKey WHERE u.id = :id")
    fun updateKeysById(
        id: UUID,
        publicKey: String,
        privateKey: String,
    )

    fun findAllByIdNot(id: UUID): List<User>

    fun findAllByUpdatedAtGreaterThan(updatedAt: Date): List<User>
}