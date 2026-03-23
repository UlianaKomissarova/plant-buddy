package dev.uliana.auth.service

import dev.uliana.auth.database.CodeType
import dev.uliana.auth.repository.CodeRepository
import dev.uliana.user.email.model.CodePayload
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.util.UUID
import org.koin.java.KoinJavaComponent.inject

const val CODE_LENGTH = 6

object CodeFactory {
    private val repository: CodeRepository by inject(CodeRepository::class.java)

    fun create(
        userId: UUID,
        type: CodeType,
        lifetime: Duration
    ): CodePayload {
        val code = buildString {
            repeat(CODE_LENGTH) {
                append(SecureRandom().nextInt(10))
            }
        }

        val hashedCode = CodeHasher.hash(code)

        val savedCode = repository.create(
            userId = userId,
            codeHash = hashedCode,
            type = type,
            expiresAt = Instant.now().plus(lifetime)
        )

        return CodePayload(code, savedCode)
    }
}
