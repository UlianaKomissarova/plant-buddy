package dev.uliana.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.tag
import dev.uliana.auth.controller.authenticatedSessionApi
import dev.uliana.auth.controller.registrationApi
import dev.uliana.auth.controller.sessionApi
import dev.uliana.auth.security.JwtAuthProvider
import dev.uliana.plant.controller.plantApi
import dev.uliana.plant.controller.wateringApi
import dev.uliana.user.controller.userApi
import dev.uliana.user.model.PrincipalUser
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.log
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.application
import io.ktor.server.routing.get

fun NormalOpenAPIRoute.swaggerRouting() {
    route("/api/v1") {
        tag(SwaggerTag.Регистрация) {
            registrationApi()
        }

        tag(SwaggerTag.Аутентификация) {
            sessionApi()
        }

        auth(JwtAuthProvider()) {
            tag(SwaggerTag.Аутентификация) {
                authenticatedSessionApi()
            }

            tag(SwaggerTag.Профиль) {
                userApi()
            }

            tag(SwaggerTag.Растения) {
                plantApi()
            }

            tag(SwaggerTag.Полив) {
                wateringApi()
            }
        }
    }
}

fun Routing.serviceRouting() {
    get("/") {
        call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
    }

    get("/openapi.json") {
        try {
            val apiObj = application.openAPIGen.api.serialize()
            val mapper = ObjectMapper().apply {
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                enable(SerializationFeature.INDENT_OUTPUT)
            }
            val json = mapper.writeValueAsString(apiObj)

            application.log.info("OpenAPI length: ${json.length}")
            application.log.info("OpenAPI head: " + json.take(1000))
            call.respondText(json, ContentType.Application.Json)
        } catch (e: Exception) {
            application.log.error("OpenAPI serialization failed", e)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "OpenAPI serialization failed: ${e.message}")
            )
        }
    }
}

inline fun NormalOpenAPIRoute.auth(
    authProvider: AuthProvider<PrincipalUser>,
    crossinline route: OpenAPIAuthenticatedRoute<PrincipalUser>.() -> Unit
): OpenAPIAuthenticatedRoute<PrincipalUser> {
    val authenticatedKtorRoute = this.ktorRoute.authenticate("auth-jwt") {}
    return OpenAPIAuthenticatedRoute(
        route = authenticatedKtorRoute,
        provider = provider.child(),
        authProvider = authProvider
    ).apply { route() }
}
