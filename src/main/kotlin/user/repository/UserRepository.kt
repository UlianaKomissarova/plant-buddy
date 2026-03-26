package dev.uliana.user.repository

import dev.uliana.user.database.UserEntity
import dev.uliana.user.database.UserTable
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

data class UpdateUserDto(
    val name: String?,
    val fcmToken: String?,
    val timezone: String?,
    val notificationTime: String?,
)

interface UserRepository {
    fun findById(id: UUID): UserEntity?
    fun update(id: UUID, dto: UpdateUserDto): UserEntity?
    fun findAllWithFcmToken(): List<UserEntity>
}

class UserRepositoryImpl : UserRepository {

    override fun findById(id: UUID): UserEntity? = transaction {
        UserEntity.findById(id)
    }

    override fun update(id: UUID, dto: UpdateUserDto): UserEntity? = transaction {
        UserEntity.findByIdAndUpdate(id) { user ->
            dto.name?.let { user.name = it }
            dto.fcmToken?.let { user.fcmToken = it }
            dto.timezone?.let { user.timezone = it }
            dto.notificationTime?.let { user.notificationTime = it }
            user.updatedAt = Instant.now()
        }
    }

    override fun findAllWithFcmToken(): List<UserEntity> = transaction {
        UserEntity.find {
            UserTable.fcmToken.isNotNull() and
                (UserTable.isConfirmed eq true) and
                (UserTable.isBlocked eq false)
        }.toList()
    }
}
