//package com.adarsh.connects_you_server.utils
//
//import org.springframework.core.MethodParameter
//import org.springframework.http.MediaType
//import org.springframework.http.converter.HttpMessageConverter
//import org.springframework.http.server.ServerHttpRequest
//import org.springframework.http.server.ServerHttpResponse
//import org.springframework.web.bind.annotation.RestControllerAdvice
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
//
//@RestControllerAdvice
//class RestAdvice : ResponseBodyAdvice<Any> {
//    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun beforeBodyWrite(
//        body: Any?,
//        returnType: MethodParameter,
//        selectedContentType: MediaType,
//        selectedConverterType: Class<out HttpMessageConverter<*>>,
//        request: ServerHttpRequest,
//        response: ServerHttpResponse
//    ): Map<String, Any>? {
//        if (body == null) {
//            return mapOf("status" to "success")
//        }
//    }
//
//}