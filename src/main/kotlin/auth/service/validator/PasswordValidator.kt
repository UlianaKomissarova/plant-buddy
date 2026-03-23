package dev.uliana.auth.service.validator

import dev.uliana.util.exception.BadRequestException

object PasswordValidator {
    private const val MIN_LENGTH = 8
    private const val MAX_LENGTH = 128

    private val hasUpperCase = Regex("[A-Z]")
    private val hasLowerCase = Regex("[a-z]")
    private val hasDigit = Regex("[0-9]")
    private val hasSpecialChar = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]")

    fun validate(password: String) {
        val errors = buildList {
            if (password.length < MIN_LENGTH)
                add("Password must be at least $MIN_LENGTH characters long")
            if (password.length > MAX_LENGTH)
                add("Password must be at most $MAX_LENGTH characters long")
            if (!hasUpperCase.containsMatchIn(password))
                add("Password must contain at least one uppercase letter")
            if (!hasLowerCase.containsMatchIn(password))
                add("Password must contain at least one lowercase letter")
            if (!hasDigit.containsMatchIn(password))
                add("Password must contain at least one digit")
            if (!hasSpecialChar.containsMatchIn(password))
                add("Password must contain at least one special character")
        }

        if (errors.isNotEmpty()) throw BadRequestException(errors.joinToString(", "))
    }
}
