package dev.uliana.auth.dto.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null,
    val timezone: String = "UTC"
)
