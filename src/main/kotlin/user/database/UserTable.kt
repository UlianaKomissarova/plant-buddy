package dev.uliana.user.database

import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object UserTable : UUIDTable("users") {
    val email = varchar("email", 1024).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val name = varchar("name", 255).nullable()
    val isConfirmed = bool("is_confirmed").default(false)
    val isBlocked = bool("is_blocked").default(false)

    val fcmToken = varchar("fcm_token", 512).nullable()
    val timezone = varchar("timezone", 100).default("UTC")

    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val deletedAt = timestamp("deleted_at").nullable()
}

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UserTable)

    var email by UserTable.email
    var passwordHash by UserTable.passwordHash
    var name by UserTable.name
    var isConfirmed by UserTable.isConfirmed
    var isBlocked by UserTable.isBlocked

    var fcmToken by UserTable.fcmToken
    var timezone by UserTable.timezone

    var createdAt by UserTable.createdAt
    var updatedAt by UserTable.updatedAt
    var deletedAt by UserTable.deletedAt
}
