package dev.uliana.auth.dto.request

data class ConfirmRequest(
    val email: String,
    val code: String
)
