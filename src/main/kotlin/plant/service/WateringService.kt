package dev.uliana.plant.service

import dev.uliana.plant.database.PlantEntity
import dev.uliana.plant.dto.request.RetroactiveWaterRequest
import dev.uliana.plant.dto.request.WaterNowRequest
import dev.uliana.plant.dto.response.ScheduleDay
import dev.uliana.plant.dto.response.ScheduleResponse
import dev.uliana.plant.dto.response.ScheduledPlant
import dev.uliana.plant.dto.response.WateringLogResponse
import dev.uliana.plant.mapper.toResponse
import dev.uliana.plant.model.ScheduleType
import dev.uliana.plant.repository.CreateWateringLogDto
import dev.uliana.plant.repository.PlantRepository
import dev.uliana.plant.repository.WateringLogRepository
import dev.uliana.util.exception.BadRequestException
import dev.uliana.util.exception.ErrorCode
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// --- DayOfWeek helpers ---

fun String.parseDaysOfWeek(): List<DayOfWeek> =
    split(",").map { DayOfWeek.valueOf(it.trim()) }

fun List<DayOfWeek>.toDaysOfWeekString(): String =
    joinToString(",") { it.name }

/** Next occurrence of any of the listed days starting from (and including) [date]. */
fun List<DayOfWeek>.nextOccurrenceFrom(date: LocalDate): LocalDate {
    var d = date
    while (d.dayOfWeek !in this) d = d.plusDays(1)
    return d
}

/** Next occurrence of any of the listed days strictly after [date]. */
fun List<DayOfWeek>.nextOccurrenceAfter(date: LocalDate): LocalDate =
    nextOccurrenceFrom(date.plusDays(1))

// --- WateringService ---

class WateringService(
    private val plantService: PlantService,
    private val plantRepository: PlantRepository,
    private val wateringLogRepository: WateringLogRepository,
) {
    suspend fun waterNow(userId: UUID, plantId: UUID, request: WaterNowRequest): WateringLogResponse =
        withContext(Dispatchers.IO) {
            val plant = plantService.getOwnedOrThrow(userId, plantId)
            val now = Instant.now()
            val nextWateringDate = plant.nextWateringDateAfter(LocalDate.now())

            val log = wateringLogRepository.create(
                CreateWateringLogDto(
                    plantId = plantId,
                    userId = userId,
                    wateredAt = now,
                    scheduledDate = plant.nextWateringDate,
                    note = request.note,
                )
            )

            plantRepository.updateNextWateringDate(plantId, nextWateringDate)

            log.toResponse()
        }

    suspend fun waterRetroactive(
        userId: UUID,
        plantId: UUID,
        request: RetroactiveWaterRequest,
    ): WateringLogResponse = withContext(Dispatchers.IO) {
        if (request.wateredAt.isAfter(Instant.now())) {
            throw BadRequestException("Watering date cannot be in the future", ErrorCode.WATERING_DATE_IN_FUTURE)
        }

        val plant = plantService.getOwnedOrThrow(userId, plantId)
        val wateredDate = request.wateredAt.atZone(ZoneOffset.UTC).toLocalDate()
        val nextWateringDate = plant.nextWateringDateAfter(wateredDate)

        val log = wateringLogRepository.create(
            CreateWateringLogDto(
                plantId = plantId,
                userId = userId,
                wateredAt = request.wateredAt,
                scheduledDate = plant.nextWateringDate,
                note = request.note,
            )
        )

        plantRepository.updateNextWateringDate(plantId, nextWateringDate)

        log.toResponse()
    }

    suspend fun getHistory(userId: UUID, plantId: UUID): List<WateringLogResponse> =
        withContext(Dispatchers.IO) {
            plantService.getOwnedOrThrow(userId, plantId)
            wateringLogRepository.findByUserIdAndPlantId(userId, plantId).map { it.toResponse() }
        }

    suspend fun getSchedule(userId: UUID, days: Int): ScheduleResponse = withContext(Dispatchers.IO) {
        if (days !in 1..365) throw BadRequestException("days must be between 1 and 365")

        val today = LocalDate.now()
        val endDate = today.plusDays(days.toLong())

        val scheduleDays = plantRepository.findAllByUserId(userId)
            .flatMap { plant -> plant.projectWateringDates(until = endDate) }
            .groupBy { it.date }
            .entries
            .sortedBy { it.key }
            .map { (date, items) -> ScheduleDay(date, items.map { it.plant }) }

        ScheduleResponse(scheduleDays)
    }

    private data class ScheduleEntry(val date: LocalDate, val plant: ScheduledPlant)

    private fun PlantEntity.projectWateringDates(until: LocalDate): List<ScheduleEntry> {
        val today = LocalDate.now()
        val daysOfWeek = wateringDaysOfWeek?.parseDaysOfWeek()
        val scheduledPlant = ScheduledPlant(
            id = id.value,
            name = name,
            iconKey = iconKey,
            quantity = quantity,
            isOverdue = false,
        )

        val entries = mutableListOf<ScheduleEntry>()
        var date = nextWateringDate

        while (!date.isAfter(until)) {
            entries += ScheduleEntry(date, scheduledPlant.copy(isOverdue = date.isBefore(today)))
            date = when (scheduleType) {
                ScheduleType.INTERVAL -> date.plusDays(wateringIntervalDays!!.toLong())
                ScheduleType.WEEKLY -> daysOfWeek!!.nextOccurrenceAfter(date)
            }
        }

        return entries
    }
}

private fun PlantEntity.nextWateringDateAfter(wateredDate: LocalDate): LocalDate =
    when (scheduleType) {
        ScheduleType.INTERVAL -> wateredDate.plusDays(wateringIntervalDays!!.toLong())
        ScheduleType.WEEKLY -> wateringDaysOfWeek!!.parseDaysOfWeek().nextOccurrenceAfter(wateredDate)
    }
