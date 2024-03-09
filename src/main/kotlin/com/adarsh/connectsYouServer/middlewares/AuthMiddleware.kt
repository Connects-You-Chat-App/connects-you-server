package com.adarsh.connectsYouServer.middlewares

import com.adarsh.connectsYouServer.annotations.AuthRequired
import com.adarsh.connectsYouServer.utils.JWTUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthMiddleware(
    private val jwtUtils: JWTUtils,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (handler is HandlerMethod && handler.method.isAnnotationPresent(AuthRequired::class.java)) {
            println("AuthMiddleware: Intercepted request to ${request.requestURI}")
            val token = request.getHeader("Authorization")
            if (token == null) {
                response.sendError(401, "Unauthorized")
                return false
            }
            try {
                val isExpirationAllowed = handler.method.getAnnotation(AuthRequired::class.java).allowExpired
                val userPayload =
                    if (isExpirationAllowed) {
                        jwtUtils.decode(token)
                    } else {
                        jwtUtils.verify(token)
                    }
                request.setAttribute("user", userPayload)
            } catch (e: Exception) {
                response.sendError(401, "Unauthorized")
                return false
            }
        }
        return true
    }
}