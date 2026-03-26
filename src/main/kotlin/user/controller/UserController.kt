package dev.uliana.user.controller

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.auth.get
import com.papsign.ktor.openapigen.route.path.auth.patch
import com.papsign.ktor.openapigen.route.path.auth.principal
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import dev.uliana.user.dto.request.UpdateProfileRequest
import dev.uliana.user.dto.response.UserProfileResponse
import dev.uliana.user.model.PrincipalUser
import dev.uliana.user.service.UserService
import org.koin.java.KoinJavaComponent.inject

fun OpenAPIAuthenticatedRoute<PrincipalUser>.userApi() {
    val userService: UserService by inject(UserService::class.java)

    route("/users/me") {
        get<Unit, UserProfileResponse, PrincipalUser>(
            info(summary = "Мой профиль")
        ) { _ ->
            respond(userService.getProfile(principal().userId))
        }

        patch<Unit, UserProfileResponse, UpdateProfileRequest, PrincipalUser>(
            info(summary = "Обновить профиль (имя, таймзона, FCM-токен)")
        ) { _, request ->
            respond(userService.updateProfile(principal().userId, request))
        }
    }
}
