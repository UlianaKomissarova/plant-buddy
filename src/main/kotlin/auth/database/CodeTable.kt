package dev.uliana.auth.database

import dev.uliana.user.database.UserTable
import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object CodeTable : UUIDTable("confirmation_codes") {
    val userId = reference(
        name = "user_id",
        foreign = UserTable,
        onDelete = ReferenceOption.CASCADE
    )

    val codeHash = text("code_hash")

    val type = enumerationByName(
        name = "type",
        length = 20,
        klass = CodeType::class
    )

    val expiresAt = timestamp("expires_at")
    val usedAt = timestamp("used_at").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}

class CodeEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CodeEntity>(CodeTable)

    var userId by CodeTable.userId
    var codeHash by CodeTable.codeHash
    var type by CodeTable.type
    var expiresAt by CodeTable.expiresAt
    var usedAt by CodeTable.usedAt
    var createdAt by CodeTable.createdAt
}

enum class CodeType {
    EMAIL_CONFIRMATION,
    PASSWORD_RESET
}
