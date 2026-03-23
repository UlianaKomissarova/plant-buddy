package dev.uliana.auth.security

import dev.uliana.user.database.UserEntity
import dev.uliana.user.mapper.toUserInfo
import dev.uliana.user.model.PrincipalUser
import io.ktor.server.auth.jwt.JWTCredential
import java.sql.Connection
import java.util.UUID
import org.jetbrains.exposed.sql.transactions.transaction

object PrincipalProvider {
    fun getFromCredential(credential: JWTCredential): PrincipalUser? {
        val purpose = credential.payload.getClaim("purpose")?.asString()
        if (purpose != "ACCESS") return null

        return transaction(transactionIsolation = Connection.TRANSACTION_READ_COMMITTED) {
            val userId = requireNotNull(UUID.fromString(credential.payload.subject))

            val user = UserEntity.findById(userId)
                ?.takeIf { it.deletedAt == null }
                ?: return@transaction null

            PrincipalUser(user.toUserInfo())
        }
    }
}
