package com.adarsh.connects_you_server.configs

import com.adarsh.connects_you_server.models.common.UserJWTClaim
import com.adarsh.connects_you_server.utils.JWTUtils
import com.corundumstudio.socketio.AuthorizationResult
import com.corundumstudio.socketio.SocketIOServer
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import com.corundumstudio.socketio.Configuration as SocketIOConfiguration
import org.springframework.context.annotation.Configuration as SpringConfiguration

@SpringConfiguration
class SocketIOConfig(
    private val jwtUtils: JWTUtils,
    @Value("\${socketio.host}") private val host: String,
    @Value("\${socketio.port}") private val port: Int
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private lateinit var server: SocketIOServer

    companion object {
        val userJWTClaims = mutableMapOf<String, UserJWTClaim>()
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

        server = SocketIOServer(config).apply {
            addConnectListener {
                println("userJWTClaims: $userJWTClaims, it: ${it.handshakeData}")
                logger.info("Client connected: ${it.remoteAddress}, Session ID: ${it.sessionId} ${userJWTClaims[it.handshakeData.toString()]}")
            }
            addDisconnectListener {
                logger.info("Client disconnected: ${it.remoteAddress}, Session ID: ${it.sessionId}")
                userJWTClaims.remove(it.toString())
            }
        }
        server.start()
        return server
    }

    @PreDestroy
    fun stopSocketIOServer() {
        if ((::server).isInitialized)
            server.stop()
    }
}