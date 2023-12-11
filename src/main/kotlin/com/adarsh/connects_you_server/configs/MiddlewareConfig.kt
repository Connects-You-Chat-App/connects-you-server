package com.adarsh.connects_you_server.configs

import com.adarsh.connects_you_server.middlewares.AuthMiddleware
import com.adarsh.connects_you_server.middlewares.ReqResMiddleware
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MiddlewareConfig(private val reqResMiddleware: ReqResMiddleware, private val authMiddleware: AuthMiddleware) :
    WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(reqResMiddleware)
        registry.addInterceptor(authMiddleware)
    }
}