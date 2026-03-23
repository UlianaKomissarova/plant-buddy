package dev.uliana.config

import dev.uliana.util.dto.MessageResponse
import dev.uliana.util.exception.ConflictException
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
            call.respond(HttpStatusCode.Conflict, MessageResponse(cause.message ?: "Conflict"))
        }

        exception<UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, MessageResponse(cause.message ?: "Unauthorized"))
        }

        exception<ForbiddenException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, MessageResponse(cause.message ?: "Forbidden"))
        }

        exception<AppNotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, MessageResponse(cause.message ?: "Not found"))
        }

        exception<AppBadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, MessageResponse(cause.message ?: "Bad request"))
        }

        // Ktor framework exceptions (e.g. malformed request body)
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, MessageResponse(cause.message ?: "Not found"))
        }

        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, MessageResponse(cause.message ?: "Bad request"))
        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, MessageResponse("Internal server error"))
        }
    }
}
