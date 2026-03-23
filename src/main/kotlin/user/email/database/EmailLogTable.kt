package dev.uliana.user.email.database

import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object EmailLogTable : UUIDTable("email_logs") {
    val email = text("email")
    val type = enumerationByName("type", 50, EmailType::class)
    val success = bool("success").nullable()
    val error = text("error").nullable()
    val createdAt = timestamp("created_at")
}

class EmailLogEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EmailLogEntity>(EmailLogTable)

    var email by EmailLogTable.email
    var type by EmailLogTable.type
    var success by EmailLogTable.success
    var error by EmailLogTable.error
    var createdAt by EmailLogTable.createdAt
}

enum class EmailType(val title: String) {
    EMAIL_CONFIRMATION("Подтверждение электронной почты Plant Buddy"),
    PASSWORD_RECOVERY("Восстановление пароля для аккаунта Plant Buddy"),
    EMAIL_CHANGE_IDENTIFICATION("Код подтверждения для смены email"),
    EMAIL_CHANGE_CONFIRMATION("Подтверждение электронной почты Plant Buddy"),
}
