package com.adarsh.connectsYouServer.annotations

@Target(
    AnnotationTarget.FUNCTION,
//        AnnotationTarget.PROPERTY_GETTER,
//        AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CLASS,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthRequired(val allowExpired: Boolean = false)