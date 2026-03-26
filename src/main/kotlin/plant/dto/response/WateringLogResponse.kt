package dev.uliana.plant.dto.response

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class WateringLogResponse(
    val id: UUID,
    val plantId: UUID,
    val wateredAt: Instant,
    val scheduledDate: LocalDate,
    val note: String?,
    val createdAt: Instant,
)
