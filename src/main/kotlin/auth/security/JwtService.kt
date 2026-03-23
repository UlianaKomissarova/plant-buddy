package dev.uliana.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.uliana.auth.database.RefreshTokenEntity
import dev.uliana.auth.dto.GeneratedToken
import dev.uliana.auth.dto.RefreshTokenCreationDto
import dev.uliana.auth.dto.response.TokenResponse
import dev.uliana.auth.repository.RefreshTokenRepository
import dev.uliana.auth.service.TokenHasher
import dev.uliana.config.AppConfig
import dev.uliana.util.exception.DateTimeFormatter
import java.time.Duration
import java.time.Instant
import java.util.Date
import java.util.UUID
import org.jetbrains.exposed.dao.id.EntityID

class JwtService(
    private val tokenRepository: RefreshTokenRepository
) {
    private val config = AppConfig.jwt
    private val algorithm = Algorithm.HMAC256(config.secret)

    private val accessTokenTtl = Duration.ofMinutes(config.accessTokenTtlMinutes)
    private val refreshTokenTtl = Duration.ofDays(config.refreshTokenTtlDays)

    fun generateTokens(userId: EntityID<UUID>): TokenResponse {
        val access = generateAccessToken(userId.value)
        val refresh = generateRefreshToken(userId)

        return TokenResponse(
            accessToken = access.token,
            accessTokenExpiresAt = DateTimeFormatter.instantToString(access.expiredAt),
            refreshToken = refresh.token,
            refreshTokenExpiresAt = DateTimeFormatter.instantToString(refresh.expiredAt),
        )
    }

    private fun generateAccessToken(userId: UUID): GeneratedToken {
        val now = Instant.now()
        val expiredAt = now.plus(accessTokenTtl)

        val token = JWT.create()
            .withIssuer(config.issuer)
            .withIssuedAt(Date.from(now))
            .withSubject(userId.toString())
            .withClaim("purpose", TokenType.ACCESS.name)
            .withJWTId(UUID.randomUUID().toString())
            .withExpiresAt(expiredAt)
            .sign(algorithm)

        return GeneratedToken(token, expiredAt)
    }

    private fun generateRefreshToken(userId: EntityID<UUID>): GeneratedToken {
        val now = Instant.now()
        val expiresAt = now.plus(refreshTokenTtl)

        val jwt = JWT.create()
            .withIssuer(config.issuer)
            .withIssuedAt(Date.from(now))
            .withSubject(userId.toString())
            .withClaim("purpose", TokenType.REFRESH.name)
            .withJWTId(UUID.randomUUID().toString())
            .withExpiresAt(expiresAt)
            .sign(algorithm)

        tokenRepository.create(
            RefreshTokenCreationDto(
                userId = userId,
                tokenHash = TokenHasher.hash(jwt),
                expiresAt = expiresAt
            )
        )

        return GeneratedToken(jwt, expiresAt)
    }

    fun getValidUserToken(userId: UUID, token: String): RefreshTokenEntity? {
        val latestToken = tokenRepository.findByUserId(userId) ?: return null

        val isInvalid = latestToken.revokedAt != null ||
                latestToken.expiresAt.isBefore(Instant.now()) ||
                !TokenHasher.verify(token, latestToken.tokenHash)

        return if (isInvalid) null else latestToken
    }

    fun revokeAllByUserId(userId: UUID) = tokenRepository.revokeAllByUserId(userId)

    private enum class TokenType { ACCESS, REFRESH }
}
