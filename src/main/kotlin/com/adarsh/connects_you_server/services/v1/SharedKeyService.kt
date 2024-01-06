package com.adarsh.connects_you_server.services.v1

import com.adarsh.connects_you_server.models.entities.Room
import com.adarsh.connects_you_server.models.entities.User
import com.adarsh.connects_you_server.models.entities.UserSharedKey
import com.adarsh.connects_you_server.models.requests.SaveSharedKeyRequest
import com.adarsh.connects_you_server.models.responses.GetUserSharedKeysResponse
import com.adarsh.connects_you_server.repositories.UserSharedKeyRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class SharedKeyService(
    private val userSharedKeyRepository: UserSharedKeyRepository,
) {
    fun getAllSharedKeys(userId: UUID): GetUserSharedKeysResponse =
        GetUserSharedKeysResponse(userSharedKeyRepository.findAllByCreatorUserId(userId))

    fun saveUserKeys(userId: UUID, keys: List<SaveSharedKeyRequest>) {
        val userSharedKeys = keys.map { key ->
            val creatorUser = User().apply {
                id = userId
            }

            val forUser = key.forUserId?.let {
                User().apply {
                    id = UUID.fromString(it)
                }
            }

            val forRoom = key.forRoomId?.let {
                Room().apply {
                    id = UUID.fromString(it)
                }
            }

            UserSharedKey(
                creatorUser = creatorUser,
                forUser = forUser,
                forRoom = forRoom,
                key = key.key
            )
        }
        userSharedKeyRepository.saveAll(userSharedKeys)
    }
}