package dev.uliana.user.email.service

import dev.uliana.user.email.model.EmailConfig
import dev.uliana.util.exception.classLogger

object EmailConfigProvider {
    fun load(): EmailConfig {
        try {
            return EmailConfig(
                host = System.getenv("EMAIL_HOST"),
                port = System.getenv("EMAIL_PORT"),
                login = System.getenv("EMAIL_LOGIN"),
                password = System.getenv("EMAIL_PASSWORD"),
                from = System.getenv("EMAIL_FROM"),
                name = System.getenv("EMAIL_NAME") ?: "Shopcore Info"
            )
        } catch (exception: Exception) {
            classLogger().error(exception.message)
            throw IllegalStateException(exception)
        }
    }
}
