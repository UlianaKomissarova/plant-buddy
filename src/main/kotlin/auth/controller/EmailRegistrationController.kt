package dev.uliana.auth.controller

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import dev.uliana.auth.dto.request.ConfirmRequest
import dev.uliana.auth.dto.request.RegisterRequest
import dev.uliana.auth.dto.response.TokenResponse
import dev.uliana.auth.service.RegistrationService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import org.koin.java.KoinJavaComponent.inject

fun NormalOpenAPIRoute.registrationApi() {
    val registrationService: RegistrationService by inject(RegistrationService::class.java)

    route("/auth") {
        route("/register") {
            post<Unit, Unit, RegisterRequest>(
                info(summary = "Регистрация пользователя")
            ) { _, request ->
                registrationService.register(request)
                pipeline.call.respond(HttpStatusCode.Created)
            }
        }

        route("/confirm") {
            post<Unit, TokenResponse, ConfirmRequest>(
                info(summary = "Подтверждение email")
            ) { _, request ->
                respond(registrationService.confirm(request))
            }
        }
    }
}
