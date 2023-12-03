package com.adarsh.chat_server.configs

import com.corundumstudio.socketio.SocketIOServer
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import com.corundumstudio.socketio.Configuration as SocketIOConfiguration
import org.springframework.context.annotation.Configuration as SpringConfiguration

@SpringConfiguration
class SocketIOConfig(
    @Value("\${socketio.host}") private val host: String,
    @Value("\${socketio.port}") private val port: Int
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private lateinit var server: SocketIOServer

    @Bean
    fun socketIOServer(): SocketIOServer {
        if ((::server).isInitialized) return server
        val config = SocketIOConfiguration()
        config.hostname = host
        config.port = port

        server = SocketIOServer(config).apply {
            addConnectListener {
                logger.info("Client connected: ${it.remoteAddress}, Session ID: ${it.sessionId}")
            }
            addDisconnectListener {
                logger.info("Client disconnected: ${it.remoteAddress}, Session ID: ${it.sessionId}")
            }
        }
        server.start()
        return server
    }

    @PreDestroy
    fun stopSocketIOServer() {
        server.stop()
    }
}