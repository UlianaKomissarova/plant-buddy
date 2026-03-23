package dev.uliana.auth.repository

import dev.uliana.auth.database.CodeType
import dev.uliana.auth.database.CodeEntity
import dev.uliana.auth.database.CodeTable
import dev.uliana.user.database.UserTable
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class CodeRepository {
    fun create(
        userId: UUID,
        codeHash: String,
        type: CodeType,
        expiresAt: Instant
    ): CodeEntity = transaction {
        CodeEntity.new {
            this.userId = EntityID(userId, UserTable)
            this.codeHash = codeHash
            this.type = type
            this.expiresAt = expiresAt
        }
    }

    fun findValidCode(
        email: String,
        codeHash: String,
        type: CodeType
    ): CodeEntity? = transaction {
        val now = Instant.now()
        (CodeTable innerJoin UserTable)
            .selectAll()
            .where {
                (UserTable.email eq email) and
                        (CodeTable.codeHash eq codeHash) and
                        (CodeTable.type eq type) and
                        (CodeTable.expiresAt greater now) and
                        (CodeTable.usedAt.isNull())
            }
            .firstOrNull()
            ?.let { CodeEntity.wrapRow(it) }
    }

    fun markCodeAsUsed(codeId: UUID) = transaction {
        CodeEntity.findByIdAndUpdate(codeId) {
            it.usedAt = Instant.now()
        }
    }
}
