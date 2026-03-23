package dev.uliana.auth.service

import dev.uliana.auth.dto.request.LoginRequest
import dev.uliana.auth.dto.request.RefreshRequest
import dev.uliana.auth.dto.response.TokenResponse
import dev.uliana.auth.repository.AuthRepository
import dev.uliana.auth.security.JwtService
import dev.uliana.auth.security.JwtVerifier
import dev.uliana.user.model.PrincipalUser
import dev.uliana.util.exception.ForbiddenException
import dev.uliana.util.exception.UnauthorizedException
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class SessionService(
    private val repository: AuthRepository,
    private val jwtService: JwtService,
) {
    suspend fun login(request: LoginRequest): TokenResponse =
        withContext(Dispatchers.IO) {
            val user = repository.findUserByEmail(request.email)
                ?: throw UnauthorizedException("Invalid credentials")

            if (!BCrypt.checkpw(request.password, user.passwordHash)) {
                throw UnauthorizedException("Invalid credentials")
            }

            if (!user.isConfirmed) {
                throw ForbiddenException("Email not confirmed")
            }

            if (user.isBlocked) {
                throw ForbiddenException("Account is blocked")
            }

            jwtService.generateTokens(user.id)
        }

    suspend fun refresh(request: RefreshRequest): TokenResponse =
        withContext(Dispatchers.IO) {
            val jwt = JwtVerifier.verify(request.refreshToken)

            val userId = UUID.fromString(jwt.subject)

            val user = repository.findUserById(userId)
                ?: throw UnauthorizedException("User not found")

            val latestToken = jwtService.getValidUserToken(userId, request.refreshToken)
                ?: throw UnauthorizedException("Invalid refresh token")

            if (user.isBlocked) throw ForbiddenException("Account is blocked")

            transaction { latestToken.revokedAt = Instant.now() }

            jwtService.generateTokens(user.id)
        }

    suspend fun logout(principal: PrincipalUser): Unit = withContext(Dispatchers.IO) {
        jwtService.revokeAllByUserId(principal.userId)
    }
}
