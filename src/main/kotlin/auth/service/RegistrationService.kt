package dev.uliana.auth.service

import dev.uliana.auth.database.CodeType
import dev.uliana.auth.dto.request.ConfirmRequest
import dev.uliana.auth.dto.request.RegisterRequest
import dev.uliana.auth.dto.response.TokenResponse
import dev.uliana.auth.repository.AuthRepository
import dev.uliana.auth.repository.CodeRepository
import dev.uliana.auth.security.JwtService
import dev.uliana.auth.service.validator.EmailValidator
import dev.uliana.auth.service.validator.PasswordValidator
import dev.uliana.user.database.UserEntity
import dev.uliana.user.email.model.EmailConfirmationNotification
import dev.uliana.util.exception.ConflictException
import dev.uliana.util.exception.ErrorCode
import dev.uliana.util.exception.UnauthorizedException
import java.time.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

private val CONFIRMATION_CODE_LIFETIME = Duration.ofMinutes(15)

class RegistrationService(
    private val repository: AuthRepository,
    private val codeRepository: CodeRepository,
    private val jwtService: JwtService,
) {
    suspend fun register(request: RegisterRequest) {
        EmailValidator.validate(request.email)
        PasswordValidator.validate(request.password)

        if (repository.findUserByEmail(request.email) != null) {
            throw ConflictException("Email already in use")
        }

        val passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())
        val user = repository.createUser(
            email = request.email,
            passwordHash = passwordHash,
            name = request.name,
            timezone = request.timezone
        )

        // TODO: отправка письма временно отключена, пользователь подтверждается автоматически
        repository.confirmUser(user.id.value)
    }

    suspend fun confirm(request: ConfirmRequest): TokenResponse = withContext(Dispatchers.IO) {
        val codeEntity = codeRepository.findValidCode(
            email = request.email,
            codeHash = CodeHasher.hash(request.code),
            type = CodeType.EMAIL_CONFIRMATION
        ) ?: throw UnauthorizedException("Invalid or expired code", ErrorCode.INVALID_CONFIRMATION_CODE)

        codeRepository.markCodeAsUsed(codeEntity.id.value)
        repository.confirmUser(codeEntity.userId.value)

        jwtService.generateTokens(codeEntity.userId)
    }

    private fun sendConfirmationCode(user: UserEntity) {
        val payload = CodeFactory.create(
            userId = user.id.value,
            type = CodeType.EMAIL_CONFIRMATION,
            lifetime = CONFIRMATION_CODE_LIFETIME
        )
        EmailConfirmationNotification(user, payload.code).send()
    }
}
