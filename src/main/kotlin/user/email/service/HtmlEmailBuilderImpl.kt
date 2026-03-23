package dev.uliana.user.email.service

import dev.uliana.user.email.model.EmailConfig
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.HtmlEmail

class HtmlEmailBuilderImpl : EmailBuilder {
    override fun build(config: EmailConfig, recipient: String, subject: String, message: String): Email {
        val authenticator = DefaultAuthenticator(config.login, config.password)
        val email = HtmlEmail()

        email.hostName = config.host
        email.setSmtpPort(config.port.toInt())
        email.authenticator = authenticator

        email.isSSLOnConnect = true
        email.isStartTLSEnabled = false

        email.setFrom(config.from, config.name)
        email.subject = subject
        email.setHtmlMsg(message)
        email.setCharset(config.charset)
        email.addTo(recipient)

        return email
    }
}
