package com.adarsh.connects_you_server.repositories

import com.adarsh.connects_you_server.models.entities.Thread
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ThreadRepository : JpaRepository<Thread, UUID>
