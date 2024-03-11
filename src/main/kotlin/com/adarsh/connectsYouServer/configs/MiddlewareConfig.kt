package com.adarsh.connectsYouServer.configs

import com.adarsh.connectsYouServer.middlewares.AuthMiddleware
import com.adarsh.connectsYouServer.middlewares.ReqResMiddleware
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MiddlewareConfig(
    private val reqResMiddleware: ReqResMiddleware,
    private val authMiddleware: AuthMiddleware,
) : WebMvcConfigurer {
    final override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(reqResMiddleware)
        registry.addInterceptor(authMiddleware)
    }
}
