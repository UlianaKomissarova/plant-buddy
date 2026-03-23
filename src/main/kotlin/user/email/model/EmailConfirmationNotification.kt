package dev.uliana.user.email.model

import dev.uliana.user.database.UserEntity
import dev.uliana.user.email.database.EmailType
import dev.uliana.user.email.model.template.EmailConfirmationTemplate

class EmailConfirmationNotification(
    private val user: UserEntity,
    private val code: String,
) : EmailNotification<String>() {

    override fun recipient(): String = user.email

    override fun prepareEmailPayload(): String = code

    override fun buildEmailMessage(payload: String): String =
        EmailConfirmationTemplate.message(payload)

    override fun emailType() = EmailType.EMAIL_CONFIRMATION
}
