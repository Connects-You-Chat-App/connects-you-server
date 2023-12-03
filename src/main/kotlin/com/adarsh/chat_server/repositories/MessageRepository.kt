package com.adarsh.chat_server.repositories

import com.adarsh.chat_server.models.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageRepository : JpaRepository<Message, UUID>
