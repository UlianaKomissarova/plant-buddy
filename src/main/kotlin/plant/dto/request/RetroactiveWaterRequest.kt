package dev.uliana.plant.dto.request

import java.time.Instant

data class RetroactiveWaterRequest(
    val wateredAt: Instant,
    val note: String? = null,
)
