package com.adarsh.connects_you_server.models.entities

import com.adarsh.connects_you_server.utils.JSON
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity(name = "users")
@Table(indexes = [Index(columnList = "email", unique = true)])
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
            updatedAt
        )
    }
}