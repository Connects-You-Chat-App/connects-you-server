package com.adarsh.connects_you_server.repositories

import com.adarsh.connects_you_server.models.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?

    @Modifying
    @Transactional
    @Query("UPDATE users u SET u.publicKey = :publicKey, u.privateKey = :privateKey WHERE u.id = :id")
    fun updateKeysById(
        @Param("id") id: UUID,
        @Param("publicKey") publicKey: String,
        @Param("privateKey") privateKey: String,
    )
}