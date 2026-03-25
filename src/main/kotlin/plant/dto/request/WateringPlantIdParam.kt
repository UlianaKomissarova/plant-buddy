package dev.uliana.plant.dto.request

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import java.util.UUID

data class WateringPlantIdParam(
    @PathParam("id") val id: UUID
)
