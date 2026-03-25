package dev.uliana.auth.service

import dev.uliana.auth.dto.request.LoginRequest
import dev.uliana.auth.dto.request.RefreshRequest
import dev.uliana.auth.dto.response.TokenResponse
import dev.uliana.auth.repository.AuthRepository
import dev.uliana.auth.security.JwtService
import dev.uliana.auth.security.JwtVerifier
import dev.uliana.user.model.PrincipalUser
import dev.uliana.util.exception.ErrorCode
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
            val passwordMatches = user != null && BCrypt.checkpw(request.password, user.passwordHash)

            if (user == null || !passwordMatches) {
                throw UnauthorizedException("Invalid credentials", ErrorCode.INVALID_CREDENTIALS)
            }

            if (!user.isConfirmed) {
                throw ForbiddenException("Email not confirmed", ErrorCode.EMAIL_NOT_CONFIRMED)
            }

            if (user.isBlocked) {
                throw ForbiddenException("Account is blocked", ErrorCode.ACCOUNT_BLOCKED)
            }

            jwtService.generateTokens(user.id)
        }

    suspend fun refresh(request: RefreshRequest): TokenResponse =
        withContext(Dispatchers.IO) {
            val jwt = JwtVerifier.verify(request.refreshToken)

            val userId = UUID.fromString(jwt.subject)

            val user = repository.findUserById(userId)
                ?: throw UnauthorizedException("Invalid credentials", ErrorCode.INVALID_CREDENTIALS)

            val latestToken = jwtService.getValidUserToken(userId, request.refreshToken)
                ?: throw UnauthorizedException("Invalid refresh token", ErrorCode.TOKEN_INVALID)

            if (user.isBlocked) throw ForbiddenException("Account is blocked", ErrorCode.ACCOUNT_BLOCKED)

            transaction { latestToken.revokedAt = Instant.now() }

            jwtService.generateTokens(user.id)
        }

    suspend fun logout(principal: PrincipalUser): Unit = withContext(Dispatchers.IO) {
        jwtService.revokeAllByUserId(principal.userId)
    }
}
