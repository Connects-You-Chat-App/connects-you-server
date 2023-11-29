package com.adarsh.chat_server.configs

import org.springframework.context.annotation.Configuration as SpringConfiguration

@SpringConfiguration
class SocketIOConfig {
//    private val logger = LoggerFactory.getLogger(javaClass)
//    private lateinit var server: SocketIOServer
//
//    @Bean
//    fun socketIOServer(): SocketIOServer {
//        if ((::server).isInitialized) return server
//        val config = SocketIOConfiguration()
//        config.hostname = host
//        config.port = port.toInt()
//
//        server = SocketIOServer(config).apply {
//            addConnectListener {
//                logger.info("Client connected: ${it.remoteAddress}, Session ID: ${it.sessionId}")
//            }
//            addDisconnectListener {
//                logger.info("Client disconnected: ${it.remoteAddress}, Session ID: ${it.sessionId}")
//            }
//        }
//        server.start()
//        return server
//    }
//
//    @PreDestroy
//    fun stopSocketIOServer() {
//        server.stop()
//    }
}