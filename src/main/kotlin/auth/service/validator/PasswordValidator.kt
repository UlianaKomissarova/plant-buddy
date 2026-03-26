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
                add("Пароль должен содержать минимум $MIN_LENGTH символов")
            if (password.length > MAX_LENGTH)
                add("Пароль должен содержать максимум $MAX_LENGTH символов")
            if (!hasUpperCase.containsMatchIn(password))
                add("Пароль должен содержать хотя бы одну заглавную букву")
            if (!hasLowerCase.containsMatchIn(password))
                add("Пароль должен содержать хотя бы одну строчную букву")
            if (!hasDigit.containsMatchIn(password))
                add("Пароль должен содержать хотя бы одну цифру")
            if (!hasSpecialChar.containsMatchIn(password))
                add("Пароль должен содержать хотя бы один специальный символ")
        }

        if (errors.isNotEmpty()) throw BadRequestException(errors.joinToString(", "))
    }
}
