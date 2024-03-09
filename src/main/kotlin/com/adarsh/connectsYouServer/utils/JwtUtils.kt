package com.adarsh.connectsYouServer.utils

import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JWTUtils(
    @Value("\${app.jwt.key}")
    private val jwtKey: String,
) {
    fun sign(claims: UserJWTClaim): String {
        return JWT.create()
            .withClaim("id", claims.id)
            .withClaim("name", claims.name)
            .withClaim("email", claims.email)
            .withClaim("deviceId", claims.deviceId)
            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 days
            .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(jwtKey))
    }

    @Throws(JWTVerificationException::class)
    fun verify(token: String): UserJWTClaim {
        val decodedToken =
            JWT.require(com.auth0.jwt.algorithms.Algorithm.HMAC256(jwtKey))
                .build()
                .verify(token)
        return UserJWTClaim(
            id = decodedToken.getClaim("id").asString(),
            name = decodedToken.getClaim("name").asString(),
            email = decodedToken.getClaim("email").asString(),
            deviceId = decodedToken.getClaim("deviceId").asString(),
        )
    }

    @Throws(JWTDecodeException::class)
    fun decode(token: String): UserJWTClaim {
        val decodedToken = JWT.decode(token)
        return UserJWTClaim(
            id = decodedToken.getClaim("id").asString(),
            name = decodedToken.getClaim("name").asString(),
            email = decodedToken.getClaim("email").asString(),
            deviceId = decodedToken.getClaim("deviceId").asString(),
        )
    }
}