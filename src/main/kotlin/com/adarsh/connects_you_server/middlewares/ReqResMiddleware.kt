package com.adarsh.connects_you_server.middlewares

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor


@Component
class ReqResMiddleware : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(ReqResMiddleware::class.java)

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        logger.info("Request Received [${request.method}] ${request.requestURL}")
        val startTime = System.currentTimeMillis()
        request.setAttribute("startTime", startTime)
        return true
    }

    @Throws(Exception::class)
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val startTime = request.getAttribute("startTime") as Long
        val endTime = System.currentTimeMillis()
        val timeTaken = endTime - startTime

        logger.info("Request processing completed for URL: " + request.requestURL.toString() + ". Total Time Taken: " + timeTaken + "ms")
    }
}