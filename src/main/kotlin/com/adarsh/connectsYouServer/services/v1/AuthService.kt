package com.adarsh.connectsYouServer.services.v1

import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.models.entities.User
import com.adarsh.connectsYouServer.models.entities.UserLoginDevice
import com.adarsh.connectsYouServer.models.requests.AuthRequest
import com.adarsh.connectsYouServer.models.requests.SaveUserKeysRequest
import com.adarsh.connectsYouServer.models.responses.AuthResponse
import com.adarsh.connectsYouServer.repositories.UserLoginDeviceRepository
import com.adarsh.connectsYouServer.repositories.UserRepository
import com.adarsh.connectsYouServer.utils.JWTUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val userLoginRepository: UserLoginDeviceRepository,
    private val jwtUtils: JWTUtils,
    @Value("\${app.hash.key}") private val hashKey: String,
    @Value("\${google.client.id}") private val googleClientId: String,
) {
    @Throws(GeneralSecurityException::class, IOException::class)
    private fun validateGoogleOAuthToken(token: String): FirebaseToken {
        return FirebaseAuth.getInstance().verifyIdToken(token)
    }

    @Transactional
    fun authenticate(authRequest: AuthRequest): AuthResponse {
        val incomingGoogleToken = authRequest.token
        val fcmToken = authRequest.fcmToken
        val deviceId = authRequest.deviceInfo.deviceId
        val userPayload = validateGoogleOAuthToken(incomingGoogleToken)
        val existingUser = userRepository.findByEmail(userPayload.email)
        val user: User
        val method: String
        if (existingUser.isPresent) {
            user = existingUser.get()
            userLoginRepository.updateFcmTokenByUserIdAndDeviceId(
                userId = user.id,
                deviceId = deviceId,
                fcmToken = fcmToken,
            )
            method = "login"
        } else {
            user =
                User(
                    email = userPayload.email,
                    isEmailVerified = userPayload.isEmailVerified,
                    authProvider = "google",
                    name = userPayload.name ?: "Unknown",
                    photoUrl = userPayload.picture,
                )
            val userLoginHistory =
                UserLoginDevice(
                    deviceId = deviceId,
                    deviceName = authRequest.deviceInfo.deviceName,
                    deviceOS = authRequest.deviceInfo.deviceOS,
                    fcmToken = fcmToken,
                    isActive = true,
                    user = user,
                )
            userRepository.save(user)
            userLoginRepository.save(userLoginHistory)
            method = "signup"
        }

        val tokenToReturn =
            jwtUtils.sign(
                UserJWTClaim(
                    id = user.id.toString(),
                    name = user.name,
                    email = user.email,
                    deviceId = deviceId,
                ),
            )

        return AuthResponse(
            token = tokenToReturn,
            method = method,
            user = user,
        )
    }

    fun signOut(user: UserJWTClaim) {
        userLoginRepository.updateIsActiveByUserIdAndDeviceId(
            userId = UUID.fromString(user.id),
            deviceId = user.deviceId,
            isActive = false,
        )
    }

    fun refreshToken(user: UserJWTClaim): AuthResponse {
        userLoginRepository.updateIsActiveByUserIdAndDeviceId(
            userId = UUID.fromString(user.id),
            deviceId = user.deviceId,
            isActive = true,
        )
        return AuthResponse(
            token =
                jwtUtils.sign(
                    UserJWTClaim(
                        id = user.id,
                        name = user.name,
                        email = user.email,
                        deviceId = user.deviceId,
                    ),
                ),
            method = "refresh",
        )
    }

    fun saveUserKeys(
        userId: UUID,
        userKeysRequest: SaveUserKeysRequest,
    ) {
        userRepository.updateKeysById(userId, userKeysRequest.publicKey, userKeysRequest.privateKey)
    }
}