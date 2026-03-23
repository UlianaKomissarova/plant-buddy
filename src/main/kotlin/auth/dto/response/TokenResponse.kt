package dev.uliana.auth.dto.response

data class TokenResponse(
    val accessToken: String,
    val accessTokenExpiresAt: String,

    val refreshToken: String,
    val refreshTokenExpiresAt: String,
)
