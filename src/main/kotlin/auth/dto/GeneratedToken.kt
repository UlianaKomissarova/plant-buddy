package dev.uliana.auth.dto

import java.time.Instant

data class GeneratedToken(
    val token: String,
    val expiredAt: Instant,
)
