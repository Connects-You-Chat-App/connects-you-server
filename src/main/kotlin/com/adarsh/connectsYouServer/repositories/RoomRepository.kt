package com.adarsh.connectsYouServer.repositories

import com.adarsh.connectsYouServer.models.entities.Room
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Date
import java.util.UUID

const val SELECTED_PROPERTIES = """
    r.id AS id, r.name AS name, r.type AS type, r.description AS description, r.logo_url AS "logoUrl", r.created_at AS "createdAt",
    ARRAY_AGG(JSONB_BUILD_OBJECT('id', u.id, 'name', u.name, 'description', u.description, 'email', u.email, 'photoUrl', u.photo_url, 'publicKey', u.public_key)) AS "roomUsers"
"""

@Repository
interface RoomRepository : JpaRepository<Room, UUID> {
    @Query(
        value = """
            SELECT $SELECTED_PROPERTIES,
            MAX(ru2.joined_at) AS "updatedAt"
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

//    @Query(
//        value = """
//            SELECT $SELECTED_PROPERTIES,
//            COALESCE(ARRAY_AGG(JSONB_BUILD_OBJECT('id', m.id, 'message', m.message, 'createdAt', m.created_at, 'updatedAt', m.updated_at, 'senderUserId', m.sender_user_id, 'type', m.type, 'belongsToMessageId', m.belongs_to_message_id, 'isDeleted', m.is_deleted, 'forwardedFromRoom', m.forwarded_from_room_id)) filter (where m.id is not null), ARRAY[]::jsonb[]) AS "messages"
//
//            FROM rooms r
//                LEFT JOIN room_users ru ON ru.room_id = r.id
//                LEFT JOIN users u ON u.id = ru.user_id
//                LEFT JOIN messages m ON m.room_id = r.id
//            WHERE r.id in :roomIds AND (
//                r.updated_at > :updatedAt OR
//                u.updated_at > :updatedAt OR
//                m.updated_at > :updatedAt OR
//                ru.joined_at > :updatedAt
//            )
//            GROUP BY r.id
//        """,
//        nativeQuery = true,
//    )
//    fun findRoomsByRoomIdsAfter(
//        roomIds: List<String>,
//        updatedAt: Date,
//    ): List<Map<String, Any>>

    @Query(
        value = """
            SELECT $SELECTED_PROPERTIES,
            MAX(r.updated_at) AS "updatedAt"
            FROM rooms r
            LEFT JOIN room_users ru ON ru.room_id = r.id
            LEFT JOIN users u ON u.id = ru.user_id
            WHERE r.id in (:roomIds) AND (r.updated_at > :updatedAt OR u.updated_at > :updatedAt OR ru.joined_at > :updatedAt)
            GROUP BY r.id
        """,
        nativeQuery = true,
    )
    fun findRoomsByRoomIdsAfter(
        roomIds: List<UUID>,
        updatedAt: Date,
    ): List<Map<String, Any>>

    @Query(
        value = """
            SELECT $SELECTED_PROPERTIES, MAX(ru.joined_at) AS "updatedAt"
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
            SELECT $SELECTED_PROPERTIES, r.updated_at AS "updatedAt"
            FROM rooms r
            JOIN room_users ru1 ON r.id = ru1.room_id AND ru1.user_id = :userId1
            JOIN room_users ru2 ON r.id = ru2.room_id AND ru2.user_id = :userId2
            LEFT JOIN users u ON u.id in (ru1.user_id, ru2.user_id)
            WHERE r.type = 'DUET'
            GROUP BY r.id;
        """,
        nativeQuery = true,
    )
    fun findCommonRoomBetween2UserIds(
        userId1: UUID,
        userId2: UUID,
    ): Map<String, Any>?

    @Query(
        value = """
            SELECT distinct r.id AS id
            FROM room_users ru
            LEFT JOIN rooms r ON r.id = ru.room_id 
            WHERE ru.user_id = :userId
        """,
        nativeQuery = true,
    )
    fun findOnlyRoomIdsByUserId(userId: UUID): List<String>
}