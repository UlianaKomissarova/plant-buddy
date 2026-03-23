package dev.uliana.user.email.model

import dev.uliana.auth.database.CodeEntity

sealed interface EmailPayload

data class CodePayload(
    val code: String,
    val savedCode: CodeEntity
) : EmailPayload
