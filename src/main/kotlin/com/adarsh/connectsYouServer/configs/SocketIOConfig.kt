package com.adarsh.connectsYouServer.configs

import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.utils.JWTUtils
import com.corundumstudio.socketio.AuthorizationResult
import com.corundumstudio.socketio.HandshakeData
import com.corundumstudio.socketio.SocketIOServer
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import com.corundumstudio.socketio.Configuration as SocketIOConfiguration
import org.springframework.context.annotation.Configuration as SpringConfiguration

@SpringConfiguration
class SocketIOConfig(
    private val jwtUtils: JWTUtils,
    @Value("\${socketio.host}") private val host: String,
    @Value("\${socketio.port}") private val port: Int,
) {
    private lateinit var server: SocketIOServer

    companion object {
        private val userJWTClaims = mutableMapOf<String, UserJWTClaim>()

        fun getUserJWTClaim(handshakeData: HandshakeData): UserJWTClaim? {
            return userJWTClaims[handshakeData.toString()]
        }

        fun removeUserJWTClaim(handshakeData: HandshakeData) {
            userJWTClaims.remove(handshakeData.toString())
        }
    }

    @Bean
    fun socketIOServer(): SocketIOServer {
        if ((::server).isInitialized) return server
        val config = SocketIOConfiguration()
        config.hostname = host
        config.port = port

        config.setAuthorizationListener {
            if (it.httpHeaders["Authorization"] == null || it.httpHeaders["Authorization"]!!.isEmpty()) {
                return@setAuthorizationListener AuthorizationResult(false)
            }
            try {
                val payload = jwtUtils.verify(it.httpHeaders["Authorization"])
                userJWTClaims[it.toString()] = payload
                return@setAuthorizationListener AuthorizationResult(true)
            } catch (e: Exception) {
                return@setAuthorizationListener AuthorizationResult(false)
            }
        }

        server = SocketIOServer(config)

        server.start()
        return server
    }

    @PreDestroy
    final fun stopSocketIOServer() {
        if ((::server).isInitialized) {
            server.stop()
        }
    }
}
