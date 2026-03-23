package dev.uliana.user.model

import java.time.Instant
import java.util.UUID

class PrincipalUser(val user: UserInfo) {
    val userId = user.userId
}

data class UserInfo(
    val userId: UUID,
    val email: String,
    val isConfirmed: Boolean,
    val isBlocked: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val deletedAt: Instant?,
)
