package com.adarsh.connects_you_server.services.v1

import com.adarsh.connects_you_server.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getUserById(id: UUID) = userRepository.findById(id)

    fun getUserByEmail(email: String) = userRepository.findByEmail(email)

    fun getAllUsersExcept(id: UUID) = userRepository.findAllByIdNot(id)
}