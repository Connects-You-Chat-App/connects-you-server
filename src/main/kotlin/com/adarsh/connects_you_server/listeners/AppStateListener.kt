package com.adarsh.connects_you_server.listeners

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.io.FileInputStream


@Component
class AppStateListener(
    @Value("\${firebase.admin.service.account.file.path}") private val serviceAccountKeyPath: String
) {
    @EventListener(ApplicationStartedEvent::class)
    fun handleAppStartedEvent() {
        val serviceAccount =
            FileInputStream(serviceAccountKeyPath)

        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
    }
}