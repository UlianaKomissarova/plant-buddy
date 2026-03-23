package dev.uliana.auth.dto

import java.util.UUID
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant

data class RefreshTokenCreationDto(
    val userId: EntityID<UUID>,
    val tokenHash: String,
    val expiresAt: Instant,
)
