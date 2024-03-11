package com.adarsh.connectsYouServer.models.entities

import com.adarsh.connectsYouServer.utils.JSON
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.Date
import java.util.UUID

@Entity(name = "users")
@Table(indexes = [Index(columnList = "email", unique = true)])
data class User(
    @Id
    var id: UUID = UUID.randomUUID(),
    @Column(updatable = false, unique = true)
    var email: String,
    var name: String,
    var photoUrl: String? = null,
    var description: String? = null,
    var publicKey: String = "",
    var privateKey: String = "",
    @JsonIgnore
    var isEmailVerified: Boolean = false,
    @JsonIgnore
    var authProvider: String = "",
    @JsonIgnore
    var locale: String? = null,
    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: Date = Date(),
    @UpdateTimestamp
    var updatedAt: Date = Date(),
) {
    constructor() : this(
        UUID.randomUUID(),
        "",
        "",
        "",
        "",
        "",
        "",
        false,
        "",
        "",
    )

    fun toMap(): Map<*, *> {
        return JSON.toMap(this)
    }

    fun toSendableMap(): Map<String, Any?> {
        val map = toMap() as MutableMap<String, Any?>
        map.remove("privateKey")
        map.remove("authProvider")
        map.remove("locale")
        map.remove("isEmailVerified")
        return map
    }

    fun toSendableObject(): User {
        return User(
            id,
            email,
            name,
            photoUrl,
            description,
            publicKey,
            "",
            isEmailVerified,
            "",
            "",
            createdAt,
            updatedAt,
        )
    }
}
