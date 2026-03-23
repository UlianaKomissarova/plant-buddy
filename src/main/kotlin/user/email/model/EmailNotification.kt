package dev.uliana.user.email.model

import dev.uliana.user.email.database.EmailType
import dev.uliana.user.email.service.EmailSender

/**
 * P - данные для генерации тела письма
 */
abstract class EmailNotification<P> {
    fun send() {
        val payload = prepareEmailPayload()
        val message = buildEmailMessage(payload)

        EmailSender.send(recipient(), emailType(), message)
    }

    protected abstract fun recipient(): String
    protected abstract fun prepareEmailPayload(): P
    protected abstract fun buildEmailMessage(payload: P): String
    protected abstract fun emailType(): EmailType
}
