package dev.uliana.plant.controller

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.auth.get
import com.papsign.ktor.openapigen.route.path.auth.post
import com.papsign.ktor.openapigen.route.path.auth.principal
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import dev.uliana.plant.dto.request.RetroactiveWaterRequest
import dev.uliana.plant.dto.request.ScheduleQueryParam
import dev.uliana.plant.dto.request.WaterNowRequest
import dev.uliana.plant.dto.request.WateringPlantIdParam
import dev.uliana.plant.dto.response.ScheduleResponse
import dev.uliana.plant.dto.response.WateringLogResponse
import dev.uliana.plant.service.WateringService
import dev.uliana.user.model.PrincipalUser
import org.koin.java.KoinJavaComponent.inject

fun OpenAPIAuthenticatedRoute<PrincipalUser>.wateringApi() {
    val wateringService: WateringService by inject(WateringService::class.java)

    route("/plants/{id}") {
        route("/water") {
            post<WateringPlantIdParam, WateringLogResponse, WaterNowRequest, PrincipalUser>(
                info(summary = "Отметить растение политым")
            ) { params, request ->
                respond(wateringService.waterNow(principal().userId, params.id, request))
            }

            route("/retroactive") {
                post<WateringPlantIdParam, WateringLogResponse, RetroactiveWaterRequest, PrincipalUser>(
                    info(summary = "Добавить полив за прошедший период")
                ) { params, request ->
                    respond(wateringService.waterRetroactive(principal().userId, params.id, request))
                }
            }
        }

        route("/watering-history") {
            get<WateringPlantIdParam, List<WateringLogResponse>, PrincipalUser>(
                info(summary = "История полива растения")
            ) { params ->
                respond(wateringService.getHistory(principal().userId, params.id))
            }
        }
    }

    route("/watering/schedule") {
        get<ScheduleQueryParam, ScheduleResponse, PrincipalUser>(
            info(summary = "Расписание полива на ближайшее время")
        ) { params ->
            respond(wateringService.getSchedule(principal().userId, params.days))
        }
    }
}
