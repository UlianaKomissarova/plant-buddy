package dev.uliana.auth.repository

import dev.uliana.user.database.UserEntity
import dev.uliana.user.database.UserTable
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.sql.transactions.transaction

class AuthRepository {
    fun findUserByEmail(email: String): UserEntity? = transaction {
        UserEntity.find { UserTable.email eq email }.firstOrNull()
    }

    fun findUserById(id: UUID): UserEntity? = transaction {
        UserEntity.findById(id)
    }

    fun createUser(
        email: String,
        passwordHash: String,
        name: String?,
        timezone: String
    ): UserEntity = transaction {
        UserEntity.new {
            this.email = email
            this.passwordHash = passwordHash
            this.name = name
            this.timezone = timezone
            this.isConfirmed = false
            this.isBlocked = false
        }
    }

    fun confirmUser(userId: UUID) = transaction {
        UserEntity.findByIdAndUpdate(userId) {
            it.isConfirmed = true
            it.updatedAt = Instant.now()
        }
    }
}
