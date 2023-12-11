package com.adarsh.connects_you_server.repositories

import com.adarsh.connects_you_server.models.entities.RoomUser
import com.adarsh.connects_you_server.models.entities.RoomUserKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomUserRepository : JpaRepository<RoomUser, RoomUserKey>
