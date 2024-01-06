package com.adarsh.connects_you_server.repositories

import com.adarsh.connects_you_server.models.entities.Room
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

const val selectProperties = """
    r.id as id, r.name as name, r.type as type, r.description as description, r.logo_url as "logoUrl", r.created_at as "createdAt",
    ARRAY_AGG(jsonb_build_object('id', u.id, 'name', u.name, 'description', u.description, 'email', u.email, 'photoUrl', u.photo_url, 'publicKey', u.public_key)) as "roomUsers"
"""

@Repository
interface RoomRepository : JpaRepository<Room, UUID> {
    @Query(
        value = """
            SELECT $selectProperties,
            MAX(ru2.joined_at) as "updatedAt"
            FROM room_users ru
            LEFT JOIN rooms r ON r.id = ru.room_id
            LEFT JOIN room_users ru2 ON ru2.room_id = r.id 
            LEFT JOIN users u ON u.id = ru2.user_id
            WHERE ru.user_id = :userId
            GROUP BY r.id
            ORDER BY "updatedAt" DESC, r.updated_at DESC
        """,
        nativeQuery = true,
    )
    fun findRoomsByUserId(userId: UUID): List<Map<String, Any>>

    @Query(
        value = """
            SELECT $selectProperties
            FROM rooms r
            LEFT JOIN room_users ru ON ru.room_id = r.id
            LEFT JOIN users u ON u.id = ru.user_id
            WHERE r.id = :roomId 
            GROUP BY r.id
        """,
        nativeQuery = true,
    )
    fun findRoomByRoomId(roomId: UUID): Map<String, Any>

    @Query(
        value = """
            SELECT $selectProperties
            FROM rooms r
            JOIN room_users ru1 ON r.id = ru1.room_id AND ru1.user_id = :userId1
            JOIN room_users ru2 ON r.id = ru2.room_id AND ru2.user_id = :userId2
            LEFT JOIN users u ON u.id in (ru1.user_id, ru2.user_id)
            WHERE r.type = 'DUET'
            GROUP BY r.id;
        """,
        nativeQuery = true,
    )
    fun findCommonRoomBetween2UserIds(userId1: UUID, userId2: UUID): Map<String, Any>?

    @Query(
        value = """
            SELECT distinct r.id as id
            FROM room_users ru
            LEFT JOIN rooms r ON r.id = ru.room_id 
            WHERE ru.user_id = :userId
        """,
        nativeQuery = true,
    )
    fun fetchOnlyRoomIdsByUserId(userId: UUID): List<String>
}