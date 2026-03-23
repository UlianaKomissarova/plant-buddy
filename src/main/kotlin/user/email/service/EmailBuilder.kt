package dev.uliana.user.email.service

import dev.uliana.user.email.model.EmailConfig
import org.apache.commons.mail.Email

interface EmailBuilder {
    fun build(
        config: EmailConfig,
        recipient: String,
        subject: String,
        message: String
    ): Email
}
