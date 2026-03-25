package dev.uliana.util.exception

class ConflictException(message: String, val code: ErrorCode = ErrorCode.EMAIL_ALREADY_REGISTERED) : Exception(message)
class UnauthorizedException(message: String, val code: ErrorCode = ErrorCode.UNAUTHORIZED) : Exception(message)
class ForbiddenException(message: String, val code: ErrorCode = ErrorCode.FORBIDDEN) : Exception(message)
class NotFoundException(message: String, val code: ErrorCode = ErrorCode.NOT_FOUND) : Exception(message)
class BadRequestException(message: String, val code: ErrorCode = ErrorCode.VALIDATION_ERROR) : Exception(message)
