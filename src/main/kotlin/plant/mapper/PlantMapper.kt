package dev.uliana.plant.mapper

import dev.uliana.plant.database.PlantEntity
import dev.uliana.plant.database.WateringLogEntity
import dev.uliana.plant.dto.response.PlantResponse
import dev.uliana.plant.dto.response.WateringLogResponse
import dev.uliana.plant.service.MoodCalculator
import dev.uliana.plant.service.parseDaysOfWeek

fun PlantEntity.toResponse(): PlantResponse = PlantResponse(
    id = this.id.value,
    name = this.name,
    iconKey = this.iconKey,
    quantity = this.quantity,
    scheduleType = this.scheduleType,
    wateringIntervalDays = this.wateringIntervalDays,
    wateringDaysOfWeek = this.wateringDaysOfWeek?.parseDaysOfWeek(),
    nextWateringDate = this.nextWateringDate,
    mood = MoodCalculator.calculate(this.nextWateringDate),
)

fun WateringLogEntity.toResponse(): WateringLogResponse = WateringLogResponse(
    id = this.id.value,
    plantId = this.plantId.value,
    wateredAt = this.wateredAt,
    scheduledDate = this.scheduledDate,
    note = this.note,
    createdAt = this.createdAt,
)
