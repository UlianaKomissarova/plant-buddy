package dev.uliana.auth.service.validator

import dev.uliana.util.exception.BadRequestException

object EmailValidator {
    private const val MAX_LENGTH = 1024

    // RFC 5322
    private val emailRegex = Regex(
        "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
    )

    fun validate(email: String) {
        val errors = buildList {
            if (email.isBlank())
                add("Email must not be blank")
            if (email.length > MAX_LENGTH)
                add("Email must be at most $MAX_LENGTH characters long")
            if (!emailRegex.matches(email.trim()))
                add("Email has invalid format")
        }

        if (errors.isNotEmpty()) throw BadRequestException(errors.joinToString(", "))
    }
}
