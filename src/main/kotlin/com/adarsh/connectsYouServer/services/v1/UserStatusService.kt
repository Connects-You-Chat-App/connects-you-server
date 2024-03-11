package com.adarsh.connectsYouServer.services.v1

import com.adarsh.connectsYouServer.models.common.KafkaMessage
import com.adarsh.connectsYouServer.models.entities.User
import com.adarsh.connectsYouServer.models.entities.UserStatus
import com.adarsh.connectsYouServer.models.enums.SocketEventType
import com.adarsh.connectsYouServer.models.requests.UserStatusRequest
import com.adarsh.connectsYouServer.repositories.UserStatusRepository
import com.adarsh.connectsYouServer.utils.KafkaUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class UserStatusService(
    private val userStatusRepository: UserStatusRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val kafkaUtils: KafkaUtils,
) {
    // TODO: Check for time for diff time zones
    fun createUserStatus(
        userId: UUID,
        userStatusRequest: UserStatusRequest,
    ): UserStatus {
        val validTill = Date(userStatusRequest.validTill)
        if (validTill.before(Date())) {
            throw Exception("Valid till date cannot be before current date")
        }

        val userStatus =
            UserStatus(
                id = UUID.randomUUID(),
                user = User().apply { id = userId },
                status = userStatusRequest.status,
                validTill = validTill,
            )

        runBlocking {
            launch {
                userStatusRepository.save(userStatus)
            }
            launch {
                redisTemplate.opsForValue()
                    .set("user_status:$userId", userStatus, userStatus.validTill.time - Date().time)
            }
            launch {
                kafkaUtils.producer!!.send(
                    kafkaUtils.createBroadcastProducerRecord(
                        KafkaMessage(
                            SocketEventType.USER_STATUS,
                            data =
                                mapOf(
                                    "userId" to userId.toString(),
                                    "status" to userStatusRequest.status,
                                    "validTill" to userStatusRequest.validTill,
                                ),
                        ),
                    ),
                )
            }
        }

        return userStatus
    }

    fun getUserStatus(userId: UUID) = userStatusRepository.findByUserId(userId)

    fun updateUserStatus(
        userId: UUID,
        userStatusRequest: UserStatusRequest,
    ) {
        val validTill = Date(userStatusRequest.validTill)
        if (validTill.before(Date())) {
            throw Exception("Valid till date cannot be before current date")
        }

        runBlocking {
            launch {
                userStatusRepository.updateByUserId(
                    userId = userId,
                    status = userStatusRequest.status,
                    validTill = validTill,
                )
            }
            launch {
                redisTemplate.opsForValue()
                    .set("user_status:$userId", userStatusRequest, validTill.time - Date().time)
            }
            launch {
                kafkaUtils.producer!!.send(
                    kafkaUtils.createBroadcastProducerRecord(
                        KafkaMessage(
                            SocketEventType.USER_STATUS,
                            data =
                                mapOf(
                                    "userId" to userId.toString(),
                                    "status" to userStatusRequest.status,
                                    "validTill" to userStatusRequest.validTill,
                                ),
                        ),
                    ),
                )
            }
        }
    }

    fun deleteUserStatus(userId: UUID) {
        runBlocking {
            launch {
                userStatusRepository.deleteByUserId(userId)
            }
            launch {
                redisTemplate.delete("user_status:$userId")
            }
            launch {
                kafkaUtils.producer!!.send(
                    kafkaUtils.createBroadcastProducerRecord(
                        KafkaMessage(
                            SocketEventType.USER_STATUS,
                            data =
                                mapOf(
                                    "userId" to userId.toString(),
                                    "status" to null,
                                ),
                        ),
                    ),
                )
            }
        }
    }
}
