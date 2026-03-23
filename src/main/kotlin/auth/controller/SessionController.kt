package dev.uliana.auth.controller

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.auth.post
import com.papsign.ktor.openapigen.route.path.auth.principal
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import dev.uliana.auth.dto.request.LoginRequest
import dev.uliana.auth.dto.request.RefreshRequest
import dev.uliana.auth.dto.response.TokenResponse
import dev.uliana.auth.service.SessionService
import dev.uliana.user.model.PrincipalUser
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import org.koin.java.KoinJavaComponent.inject

fun OpenAPIAuthenticatedRoute<PrincipalUser>.authenticatedSessionApi() {
    val sessionService: SessionService by inject(SessionService::class.java)

    route("/auth/logout") {
        post<Unit, Unit, Unit, PrincipalUser>(
            info(summary = "Выйти из аккаунта")
        ) { _, _ ->
            sessionService.logout(principal())
            pipeline.call.respond(HttpStatusCode.NoContent)
        }
    }
}

fun NormalOpenAPIRoute.sessionApi() {
    val sessionService: SessionService by inject(SessionService::class.java)

    route("/auth") {
        route("/login") {
            route("/email") {
                post<Unit, TokenResponse, LoginRequest>(
                    info(summary = "Войти через электронную почту и пароль")
                ) { _, request ->
                    respond(sessionService.login(request))
                }
            }
        }

        route("/tokens") {
            route("/refresh") {
                post<Unit, TokenResponse, RefreshRequest>(
                    info(summary = "Обновить токены с помощью refresh-токена")
                ) { _, body ->
                    respond(sessionService.refresh(body))
                }
            }
        }
    }
}
