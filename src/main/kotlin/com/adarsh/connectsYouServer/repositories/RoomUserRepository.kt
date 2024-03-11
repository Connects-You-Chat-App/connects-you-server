package com.adarsh.connectsYouServer.repositories

import com.adarsh.connectsYouServer.models.entities.RoomUser
import com.adarsh.connectsYouServer.models.entities.RoomUserKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomUserRepository : JpaRepository<RoomUser, RoomUserKey>
