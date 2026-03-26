package dev.uliana.plant.dto.response

import java.time.LocalDate
import java.util.UUID

data class ScheduleResponse(
    val days: List<ScheduleDay>,
)

data class ScheduleDay(
    val date: LocalDate,
    val plants: List<ScheduledPlant>,
)

data class ScheduledPlant(
    val id: UUID,
    val name: String,
    val iconKey: String,
    val quantity: Int,
    val isOverdue: Boolean,
)
