package dev.uliana.plant.dto.request

import com.papsign.ktor.openapigen.annotations.parameters.QueryParam

data class ScheduleQueryParam(
    @QueryParam("days") val days: Int = 30
)
