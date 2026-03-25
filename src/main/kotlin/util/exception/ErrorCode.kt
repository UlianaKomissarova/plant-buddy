package dev.uliana.util.exception

enum class ErrorCode {
    // Registration
    EMAIL_ALREADY_REGISTERED,
    INVALID_CONFIRMATION_CODE,

    // Authentication
    INVALID_CREDENTIALS,
    EMAIL_NOT_CONFIRMED,
    ACCOUNT_BLOCKED,
    TOKEN_INVALID,

    // Plants
    PLANT_NOT_FOUND,
    PLANT_ACCESS_DENIED,

    // Watering
    WATERING_DATE_IN_FUTURE,

    // Generic
    VALIDATION_ERROR,
    NOT_FOUND,
    UNAUTHORIZED,
    FORBIDDEN,
    INTERNAL_ERROR,
}
