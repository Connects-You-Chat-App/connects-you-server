package com.adarsh.connectsYouServer.services.v1

import com.adarsh.connectsYouServer.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun fetchUserById(id: UUID) = userRepository.findById(id)

    fun fetchUserByEmail(email: String) = userRepository.findByEmail(email)

    fun fetchAllUsersExcept(id: UUID) = userRepository.findAllByIdNot(id)
}
