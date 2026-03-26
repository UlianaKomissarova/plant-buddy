package dev.uliana.plant.controller

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.auth.delete
import com.papsign.ktor.openapigen.route.path.auth.get
import com.papsign.ktor.openapigen.route.path.auth.patch
import com.papsign.ktor.openapigen.route.path.auth.post
import com.papsign.ktor.openapigen.route.path.auth.principal
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import dev.uliana.plant.dto.request.CreatePlantRequest
import dev.uliana.plant.dto.request.PlantIdParam
import dev.uliana.plant.dto.request.UpdatePlantRequest
import dev.uliana.plant.dto.response.PlantResponse
import dev.uliana.plant.service.PlantService
import dev.uliana.user.model.PrincipalUser
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import org.koin.java.KoinJavaComponent.inject

fun OpenAPIAuthenticatedRoute<PrincipalUser>.plantApi() {
    val plantService: PlantService by inject(PlantService::class.java)

    route("/plants") {
        post<Unit, PlantResponse, CreatePlantRequest, PrincipalUser>(
            info(summary = "Добавить растение")
        ) { _, request ->
            respond(plantService.create(principal().userId, request))
        }

        get<Unit, List<PlantResponse>, PrincipalUser>(
            info(summary = "Список всех растений с настроением")
        ) { _ ->
            respond(plantService.getAll(principal().userId))
        }

        route("/{id}") {
            get<PlantIdParam, PlantResponse, PrincipalUser>(
                info(summary = "Информация о растении и его настроение")
            ) { params ->
                respond(plantService.getOne(principal().userId, params.id))
            }

            patch<PlantIdParam, PlantResponse, UpdatePlantRequest, PrincipalUser>(
                info(summary = "Обновить растение")
            ) { params, request ->
                respond(plantService.update(principal().userId, params.id, request))
            }

            delete<PlantIdParam, Unit, PrincipalUser>(
                info(summary = "Удалить растение")
            ) { params ->
                plantService.delete(principal().userId, params.id)
                pipeline.call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
