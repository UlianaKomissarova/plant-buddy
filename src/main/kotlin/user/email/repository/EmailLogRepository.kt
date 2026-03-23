package dev.uliana.user.email.repository

import dev.uliana.user.email.database.EmailLogEntity
import dev.uliana.user.email.database.EmailLogTable
import dev.uliana.user.email.database.EmailType
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

interface EmailLogRepository {
    fun create(recipient: String, emailType: EmailType): UUID

    fun markAsSuccess(logId: UUID)

    fun markAsFailed(logId: UUID, message: String?)

    fun findLastSuccessfulSendTimeByType(email: String, type: EmailType): Instant?

    fun findLastSuccessfulSendTimes(
        email: String,
        type: EmailType,
        sinceTime: Instant,
        limit: Int,
    ): List<Instant>
}

class EmailLogRepositoryImpl : EmailLogRepository {
    override fun create(recipient: String, emailType: EmailType): UUID = transaction {
        EmailLogEntity.new {
            this.email = recipient
            type = emailType
            createdAt = Instant.now()
        }.id.value
    }

    override fun markAsSuccess(logId: UUID) = transaction {
        val log = findById(logId) ?: return@transaction
        log.success = true
    }

    override fun markAsFailed(logId: UUID, message: String?) = transaction {
        val log = findById(logId) ?: return@transaction

        log.error = message
        log.success = false
    }

    override fun findLastSuccessfulSendTimeByType(email: String, type: EmailType): Instant? = transaction {
        EmailLogTable
            .select(EmailLogTable.createdAt)
            .where {
                EmailLogTable.email eq email and
                        (EmailLogTable.type eq type) and
                        (EmailLogTable.success eq true)
            }
            .orderBy(EmailLogTable.createdAt, SortOrder.DESC)
            .limit(1)
            .firstOrNull()
            ?.get(EmailLogTable.createdAt)
    }

    override fun findLastSuccessfulSendTimes(
        email: String,
        type: EmailType,
        sinceTime: Instant,
        limit: Int,
    ): List<Instant> = transaction {
        EmailLogTable
            .select(EmailLogTable.createdAt)
            .where(
                EmailLogTable.success eq true and
                        (EmailLogTable.email eq email) and
                        (EmailLogTable.type eq type) and
                        (EmailLogTable.createdAt greaterEq sinceTime)
            )
            .orderBy(EmailLogTable.createdAt, SortOrder.DESC)
            .limit(limit)
            .map { it[EmailLogTable.createdAt] }
    }

    private fun findById(logId: UUID): EmailLogEntity? = transaction {
        EmailLogEntity.findById(logId)
    }
}
