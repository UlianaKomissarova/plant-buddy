package dev.uliana.auth.security

import com.papsign.ktor.openapigen.model.Described
import com.papsign.ktor.openapigen.model.security.HttpSecurityScheme
import com.papsign.ktor.openapigen.model.security.SecuritySchemeModel
import com.papsign.ktor.openapigen.model.security.SecuritySchemeType
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import dev.uliana.user.model.PrincipalUser
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.routing.RoutingContext
import javax.security.sasl.AuthenticationException

class JwtAuthProvider : AuthProvider<PrincipalUser> {
    override fun apply(route: NormalOpenAPIRoute): OpenAPIAuthenticatedRoute<PrincipalUser> {
        val ktRoute = route.ktorRoute.authenticate("auth-jwt") {}
        return OpenAPIAuthenticatedRoute(ktRoute, route.provider.child(), this)
    }

    override suspend fun getAuth(pipeline: RoutingContext): PrincipalUser {
        return pipeline.call.authentication.principal()
            ?: throw AuthenticationException(HttpStatusCode.Unauthorized.description)
    }

    override val security: Iterable<Iterable<AuthProvider.Security<*>>>
        get() = listOf(
            listOf(
                AuthProvider.Security(
                    SecuritySchemeModel(
                        SecuritySchemeType.http,
                        scheme = HttpSecurityScheme.bearer,
                        bearerFormat = "JWT",
                        name = "jwtAuth",
                        referenceName = "JWT"
                    ), emptyList<Scopes>()
                )
            )
        )

    private enum class Scopes(override val description: String) : Described
}
