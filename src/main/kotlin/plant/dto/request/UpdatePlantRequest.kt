package dev.uliana.plant.dto.request

import dev.uliana.plant.model.ScheduleType
import java.time.DayOfWeek
import java.time.LocalDate

data class UpdatePlantRequest(
    val name: String?,
    val iconKey: String?,
    val quantity: Int?,
    val scheduleType: ScheduleType?,
    val wateringIntervalDays: Int?,
    val wateringDaysOfWeek: List<DayOfWeek>?,
    val nextWateringDate: LocalDate?,
)
