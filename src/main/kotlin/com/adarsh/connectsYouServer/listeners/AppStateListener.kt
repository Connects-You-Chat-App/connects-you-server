package com.adarsh.connectsYouServer.listeners

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
    @Value("\${firebase.admin.service.account.file.path}") private val serviceAccountKeyPath: String,
) {
    @EventListener(ApplicationStartedEvent::class)
    final fun handleAppStartedEvent() {
        val serviceAccount = FileInputStream(serviceAccountKeyPath)

        val options =
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

        FirebaseApp.initializeApp(options)
    }
}
