package com.adarsh.connects_you_server.middlewares

import com.adarsh.connects_you_server.annotations.IsAuthRequired
import com.adarsh.connects_you_server.utils.JWTUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthMiddleware(
    private val jwtUtils: JWTUtils
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.method.isAnnotationPresent(IsAuthRequired::class.java)) {
            val token = request.getHeader("Authorization")
            if (token == null) {
                response.sendError(401, "Unauthorized")
                return false
            }
            try {
                val userPayload = jwtUtils.verify(token)
                request.setAttribute("user", userPayload)
            } catch (e: Exception) {
                response.sendError(401, "Unauthorized")
                return false
            }
        }
        return true
    }
}