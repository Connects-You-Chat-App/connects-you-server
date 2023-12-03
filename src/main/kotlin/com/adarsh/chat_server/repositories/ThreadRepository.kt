package com.adarsh.chat_server.repositories

import com.adarsh.chat_server.models.Thread
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ThreadRepository : JpaRepository<Thread, UUID>
