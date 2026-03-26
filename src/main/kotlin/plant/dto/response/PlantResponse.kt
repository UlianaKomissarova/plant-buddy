package dev.uliana.plant.dto.response

import dev.uliana.plant.model.PlantMood
import dev.uliana.plant.model.ScheduleType
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

data class PlantResponse(
    val id: UUID,
    val name: String,
    val iconKey: String,
    val quantity: Int,
    val scheduleType: ScheduleType,
    val wateringIntervalDays: Int?,
    val wateringDaysOfWeek: List<DayOfWeek>?,
    val nextWateringDate: LocalDate,
    val mood: PlantMood,
)
