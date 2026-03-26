package dev.uliana.plant.dto.request

import dev.uliana.plant.model.ScheduleType
import java.time.DayOfWeek
import java.time.LocalDate

data class CreatePlantRequest(
    val name: String,
    val iconKey: String,
    val quantity: Int = 1,
    val scheduleType: ScheduleType,
    /** Required when scheduleType = INTERVAL. */
    val wateringIntervalDays: Int? = null,
    /** Required when scheduleType = WEEKLY. */
    val wateringDaysOfWeek: List<DayOfWeek>? = null,
    /** Required when scheduleType = INTERVAL. Ignored for WEEKLY (auto-calculated). */
    val firstWateringDate: LocalDate? = null,
)
