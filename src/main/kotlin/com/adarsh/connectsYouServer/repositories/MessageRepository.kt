package com.adarsh.connectsYouServer.repositories

import com.adarsh.connectsYouServer.models.entities.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Date
import java.util.UUID

@Repository
interface MessageRepository : JpaRepository<Message, UUID> {
    fun findByRoomId(roomId: UUID): List<Message>

    @Query(
        value = """
            select 
                m.id as "id",
                m.message as "message",
                m.created_at as "createdAt",
                m.updated_at as "updatedAt",
                m.type as "type",
                m.belongs_to_message_id as "belongsToMessageId",
                m.sender_user_id as "senderUserId",
                m.is_deleted as "isDeleted",
                m.forwarded_from_room_id as "forwardedFromRoomId",
                m.edited_at as "editedAt",
                ARRAY_AGG(JSONB_BUILD_OBJECT(
                    'id', u.id,
                    'name', u.name,
                    'description', u.description,
                    'email', u.email,
                    'photoUrl', u.photo_url,
                    'publicKey', u.public_key
                )) AS "users",
                ARRAY_AGG(JSONB_BUILD_OBJECT(
                    'id', r.id,
                    'name', r.name,
                    'type', r.type,
                    'logoUrl', r.logo_url
                )) AS "room"
            from messages m
            left join rooms r on r.id = m.room_id
            left join room_users ru on ru.room_id = r.id and (r.type = 'DUET' or m.sender_user_id = ru.user_id)
            left join users u on u.id = ru.user_id
            where m.room_id in :roomIds and m.updated_at > :updatedAt
            group by m.id
        """,
        nativeQuery = true,
    )
    fun findMessagesByRoomIdsAfter(
        roomIds: List<UUID>,
        updatedAt: Date,
    ): List<Map<*, *>>
}