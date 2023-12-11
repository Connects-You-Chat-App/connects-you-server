package com.adarsh.connects_you_server.services.v1

import com.adarsh.connects_you_server.models.common.UserJWTClaim
import com.adarsh.connects_you_server.models.entities.User
import com.adarsh.connects_you_server.models.entities.UserLoginDevice
import com.adarsh.connects_you_server.models.requests.AuthRequest
import com.adarsh.connects_you_server.models.requests.SaveUserKeysRequest
import com.adarsh.connects_you_server.models.responses.AuthResponse
import com.adarsh.connects_you_server.repositories.UserLoginDeviceRepository
import com.adarsh.connects_you_server.repositories.UserRepository
import com.adarsh.connects_you_server.utils.JWTUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.*


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val userLoginRepository: UserLoginDeviceRepository,
    private val jwtUtils: JWTUtils,
    @Value("\${app.hash.key}") private val hashKey: String,
    @Value("\${google.client.id}") private val googleClientId: String
) {
    @Throws(GeneralSecurityException::class, IOException::class)
    private fun validateGoogleOAuthToken(token: String): FirebaseToken {
        return FirebaseAuth.getInstance().verifyIdToken(token)
    }

    fun authenticate(authRequest: AuthRequest): AuthResponse {
        val incomingGoogleToken = authRequest.token
        val fcmToken = authRequest.fcmToken
        val deviceId = authRequest.deviceInfo.deviceId
        val userPayload = validateGoogleOAuthToken(incomingGoogleToken)
        val existingUser = userRepository.findByEmail(userPayload.email)
        val user: User
        val method: String
        if (existingUser == null) {
            user = User(
                email = userPayload.email,
                isEmailVerified = userPayload.isEmailVerified,
                authProvider = "google",
                name = userPayload.name,
                photoUrl = userPayload.picture
            )
            val userLoginHistory = UserLoginDevice(
                deviceId = deviceId,
                deviceName = authRequest.deviceInfo.deviceName,
                deviceOS = authRequest.deviceInfo.deviceOS,
                fcmToken = fcmToken,
                isActive = true,
                user = user
            )
            userRepository.save(user)
            userLoginRepository.save(userLoginHistory)
            method = "signup"
        } else {
            user = existingUser
            userLoginRepository.updateFcmTokenByUserIdAndDeviceId(
                userId = user.id,
                deviceId = deviceId,
                fcmToken = fcmToken
            )

            method = "login"
        }


        val tokenToReturn = jwtUtils.sign(
            UserJWTClaim(
                id = user.id.toString(),
                name = user.name,
                email = user.email,
                deviceId = deviceId,
            )
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
            isActive = false
        )
    }

    fun saveUserKeys(userId: UUID, userKeysRequest: SaveUserKeysRequest) {
        userRepository.updateKeysById(userId, userKeysRequest.publicKey, userKeysRequest.privateKey)
    }
}