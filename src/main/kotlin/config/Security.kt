package dev.uliana.config

import dev.uliana.auth.security.JwtVerifier
import dev.uliana.auth.security.PrincipalProvider
import dev.uliana.util.dto.MessageResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JwtVerifier.getVerifier())
            validate { PrincipalProvider.getFromCredential(it) }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, MessageResponse("Token is invalid or expired"))
            }
        }
    }
}
