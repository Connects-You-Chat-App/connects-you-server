package com.adarsh.chat_server.repositories

import com.adarsh.chat_server.models.RoomUser
import com.adarsh.chat_server.models.RoomUserKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomUserRepository : JpaRepository<RoomUser, RoomUserKey>
