package dev.uliana.auth.repository

import dev.uliana.auth.database.RefreshTokenEntity
import dev.uliana.auth.database.RefreshTokenTable
import dev.uliana.auth.dto.RefreshTokenCreationDto
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

interface RefreshTokenRepository {
    fun create(token: RefreshTokenCreationDto)

    fun findByUserId(userId: UUID): RefreshTokenEntity?

    fun revokeAllByUserId(userId: UUID)
}

class RefreshTokenRepositoryImpl : RefreshTokenRepository {
    override fun create(token: RefreshTokenCreationDto): Unit = transaction {
        RefreshTokenEntity.new {
            this.userId = token.userId
            this.tokenHash = token.tokenHash
            this.expiresAt = token.expiresAt
        }
    }

    override fun findByUserId(userId: UUID): RefreshTokenEntity? = transaction {
        RefreshTokenEntity
            .find {
                (RefreshTokenTable.userId eq userId) and
                        (RefreshTokenTable.revokedAt.isNull()) and
                        (RefreshTokenTable.expiresAt greater Instant.now())
            }
            .orderBy(RefreshTokenTable.expiresAt to SortOrder.DESC)
            .firstOrNull()
    }

    override fun revokeAllByUserId(userId: UUID): Unit = transaction {
        RefreshTokenTable.update(
            where = {
                RefreshTokenTable.userId eq userId and
                        (RefreshTokenTable.expiresAt.greater(Instant.now()) and
                                (RefreshTokenTable.revokedAt.isNull()))
            }
        ) {
            it[RefreshTokenTable.revokedAt] = Instant.now()
        }
    }
}
