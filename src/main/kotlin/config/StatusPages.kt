package dev.uliana.config

import dev.uliana.util.dto.ErrorResponse
import dev.uliana.util.exception.ConflictException
import dev.uliana.util.exception.ErrorCode
import dev.uliana.util.exception.ForbiddenException
import dev.uliana.util.exception.UnauthorizedException
import dev.uliana.util.exception.NotFoundException as AppNotFoundException
import dev.uliana.util.exception.BadRequestException as AppBadRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ConflictException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, cause.toResponse())
        }

        exception<UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, cause.toResponse())
        }

        exception<ForbiddenException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, cause.toResponse())
        }

        exception<AppNotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, cause.toResponse())
        }

        exception<AppBadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.toResponse())
        }

        // Ktor framework exceptions (e.g. malformed request body)
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(ErrorCode.NOT_FOUND.name, cause.message ?: "Not found"))
        }

        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(ErrorCode.VALIDATION_ERROR.name, cause.message ?: "Bad request"))
        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(ErrorCode.INTERNAL_ERROR.name, "Internal server error"))
        }
    }
}

private fun ConflictException.toResponse() = ErrorResponse(code.name, message ?: "Conflict")
private fun UnauthorizedException.toResponse() = ErrorResponse(code.name, message ?: "Unauthorized")
private fun ForbiddenException.toResponse() = ErrorResponse(code.name, message ?: "Forbidden")
private fun AppNotFoundException.toResponse() = ErrorResponse(code.name, message ?: "Not found")
private fun AppBadRequestException.toResponse() = ErrorResponse(code.name, message ?: "Bad request")
