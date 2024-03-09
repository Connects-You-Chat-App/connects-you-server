package com.adarsh.connectsYouServer.services.v1

import com.adarsh.connectsYouServer.models.entities.Room
import com.adarsh.connectsYouServer.models.entities.User
import com.adarsh.connectsYouServer.models.entities.UserSharedKey
import com.adarsh.connectsYouServer.models.requests.SaveSharedKeyRequest
import com.adarsh.connectsYouServer.repositories.UserSharedKeyRepository
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class SharedKeyService(
    private val userSharedKeyRepository: UserSharedKeyRepository,
) {
    fun fetchSharedKeysByUserId(userId: UUID) = userSharedKeyRepository.findAllByCreatorUserId(userId)

    fun saveUserKeys(
        userId: UUID,
        keys: List<SaveSharedKeyRequest>,
    ) {
        val userSharedKeys =
            keys.map { key ->
                val creatorUser =
                    User().apply {
                        id = userId
                    }

                val forUser =
                    key.forUserId?.let {
                        User().apply {
                            id = UUID.fromString(it)
                        }
                    }

                val forRoom =
                    key.forRoomId?.let {
                        Room().apply {
                            id = UUID.fromString(it)
                        }
                    }

                UserSharedKey(
                    creatorUser = creatorUser,
                    forUser = forUser,
                    forRoom = forRoom,
                    key = key.key,
                )
            }
        userSharedKeyRepository.saveAll(userSharedKeys)
    }

    fun fetchUpdatedDataAfter(
        userId: UUID,
        updatedAt: Date,
    ) = userSharedKeyRepository.findAllByCreatorUserAndUpdatedAtGreaterThan(User().apply { id = userId }, updatedAt)
}