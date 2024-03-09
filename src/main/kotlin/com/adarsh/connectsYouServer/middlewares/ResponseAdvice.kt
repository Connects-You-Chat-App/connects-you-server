package com.adarsh.connectsYouServer.middlewares

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
class ResponseAdvice : ResponseBodyAdvice<Any> {
    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>,
    ) = true

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ): Any? {
        val servletResponse = (response as ServletServerHttpResponse).servletResponse
        var bodyToReturn =
            if (servletResponse.status >= 500) {
                mapOf("message" to "Something went wrong")
            } else if (servletResponse.status in 400..499) {
                when (servletResponse.status) {
                    400 -> mapOf("message" to "Bad Request")
                    401 -> mapOf("message" to "Unauthorized")
                    403 -> mapOf("message" to "Forbidden")
                    404 -> mapOf("message" to "Not Found")
                    409 -> mapOf("message" to "Resource already exists")
                    else -> mapOf("message" to "Something went wrong")
                }
            } else if (servletResponse.status in 200..399) {
                body ?: mapOf("message" to "Success")
            } else {
                body
            }
        if (bodyToReturn == null) {
            bodyToReturn =
                mapOf("message" to "Something went wrong")
        }

        return bodyToReturn
    }
}