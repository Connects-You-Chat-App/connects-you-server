package com.adarsh.connectsYouServer.listeners

import com.adarsh.connectsYouServer.services.v1.UserStatusService
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RedisListener(
    private val userStatusService: UserStatusService,
) {
    @Bean
    fun keyExpirationListenerContainer(connectionFactory: RedisConnectionFactory): RedisMessageListenerContainer {
        println("Initializing RedisMessageListenerContainer...")
        val listenerContainer = RedisMessageListenerContainer()
        listenerContainer.setConnectionFactory(connectionFactory)

        listenerContainer.addMessageListener(
            { message: Message?, _: ByteArray? ->
                println("Received expired event for key: $message")
                if (message != null) {
                    if (message.toString().startsWith("user_status:")) {
                        val userId = message.toString().split(":")[1]
                        userStatusService.deleteUserStatus(UUID.fromString(userId))
                    }
                }
            },
            PatternTopic("__keyevent@*__:expired"),
        )

        return listenerContainer
    }
}
