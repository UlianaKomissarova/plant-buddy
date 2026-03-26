package dev.uliana.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import dev.uliana.config.AppConfig
import dev.uliana.util.exception.classLogger

object FcmService {
    private val logger = classLogger()

    fun init() {
        val json = AppConfig.fcm.serviceAccountJson
        if (json.isBlank()) {
            logger.warn("FCM_SERVICE_ACCOUNT_JSON is not set — push notifications are disabled")
            return
        }

        try {
            val credentials = GoogleCredentials.fromStream(json.byteInputStream())
            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()
            FirebaseApp.initializeApp(options)
            logger.info("Firebase initialized successfully")
        } catch (e: Exception) {
            logger.error("Failed to initialize Firebase: ${e.message}")
        }
    }

    fun isEnabled(): Boolean = try {
        FirebaseApp.getInstance()
        true
    } catch (_: Exception) {
        false
    }

    fun send(fcmToken: String, title: String, body: String) {
        if (!isEnabled()) return

        try {
            val message = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .build()

            FirebaseMessaging.getInstance().send(message)
        } catch (e: Exception) {
            logger.error("Failed to send FCM notification: ${e.message}")
        }
    }
}
