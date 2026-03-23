package dev.uliana.user.email.service

import com.google.common.util.concurrent.ThreadFactoryBuilder
import dev.uliana.user.email.database.EmailType
import dev.uliana.user.email.repository.EmailLogRepository
import dev.uliana.util.exception.classLogger
import java.util.concurrent.Executors
import org.koin.java.KoinJavaComponent.inject

object EmailSender {
    private val logger = classLogger()
    private val repository: EmailLogRepository by inject(EmailLogRepository::class.java)
    private val emailBuilder: EmailBuilder by inject(EmailBuilder::class.java)

    private val executor = Executors.newFixedThreadPool(
        2,
        ThreadFactoryBuilder()
            .setNameFormat("email-sender")
            .setDaemon(true)
            .setUncaughtExceptionHandler { _, exception ->
                logger.error(exception.message)
            }
            .build()
    )

    fun send(recipient: String, emailType: EmailType, message: String) {
        val subject = emailType.title
        val logID = repository.create(recipient, emailType)
        val emailConfig = EmailConfigProvider.load()

        executor.execute {
            try {
                val email = emailBuilder.build(emailConfig, recipient, subject, message)
                email.send()

                repository.markAsSuccess(logID)

            } catch (exception: Exception) {
                try {
                    repository.markAsFailed(logID, exception.message)
                } catch (repositoryException: Exception) {
                    logger.error(repositoryException.message)
                }

                logger.error(exception.message)
            }
        }
    }
}
