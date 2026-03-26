package dev.uliana.plant.service

import dev.uliana.plant.database.PlantEntity
import dev.uliana.plant.dto.request.CreatePlantRequest
import dev.uliana.plant.dto.request.UpdatePlantRequest
import dev.uliana.plant.dto.response.PlantResponse
import dev.uliana.plant.mapper.toResponse
import dev.uliana.plant.model.ScheduleType
import dev.uliana.plant.repository.CreatePlantDto
import dev.uliana.plant.repository.PlantRepository
import dev.uliana.plant.repository.UpdatePlantDto
import dev.uliana.util.exception.BadRequestException
import dev.uliana.util.exception.ErrorCode
import dev.uliana.util.exception.ForbiddenException
import dev.uliana.util.exception.NotFoundException
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlantService(private val repository: PlantRepository) {

    suspend fun create(userId: UUID, request: CreatePlantRequest): PlantResponse =
        withContext(Dispatchers.IO) {
            request.validate()
            repository.create(request.toDto(userId)).toResponse()
        }

    suspend fun getAll(userId: UUID): List<PlantResponse> = withContext(Dispatchers.IO) {
        repository.findAllByUserId(userId).map { it.toResponse() }
    }

    suspend fun getOne(userId: UUID, plantId: UUID): PlantResponse = withContext(Dispatchers.IO) {
        getOwnedOrThrow(userId, plantId).toResponse()
    }

    suspend fun update(userId: UUID, plantId: UUID, request: UpdatePlantRequest): PlantResponse =
        withContext(Dispatchers.IO) {
            getOwnedOrThrow(userId, plantId)
            request.validate()
            repository.update(plantId, request.toDto())?.toResponse()
                ?: throw NotFoundException("Plant not found", ErrorCode.PLANT_NOT_FOUND)
        }

    suspend fun delete(userId: UUID, plantId: UUID): Unit = withContext(Dispatchers.IO) {
        getOwnedOrThrow(userId, plantId)
        repository.softDelete(plantId)
    }

    fun getOwnedOrThrow(userId: UUID, plantId: UUID): PlantEntity {
        val plant = repository.findById(plantId) ?: throw NotFoundException("Plant not found", ErrorCode.PLANT_NOT_FOUND)
        if (plant.userId.value != userId) throw ForbiddenException("Access denied", ErrorCode.PLANT_ACCESS_DENIED)
        return plant
    }

    private fun CreatePlantRequest.toDto(userId: UUID) = CreatePlantDto(
        userId = userId,
        name = name,
        iconKey = iconKey,
        quantity = quantity,
        scheduleType = scheduleType,
        wateringIntervalDays = wateringIntervalDays,
        wateringDaysOfWeek = wateringDaysOfWeek?.toDaysOfWeekString(),
        nextWateringDate = when (scheduleType) {
            ScheduleType.INTERVAL -> firstWateringDate!!
            ScheduleType.WEEKLY -> wateringDaysOfWeek!!.nextOccurrenceFrom(LocalDate.now())
        },
    )

    private fun UpdatePlantRequest.toDto() = UpdatePlantDto(
        name = name,
        iconKey = iconKey,
        quantity = quantity,
        scheduleType = scheduleType,
        wateringIntervalDays = wateringIntervalDays,
        wateringDaysOfWeek = wateringDaysOfWeek?.toDaysOfWeekString(),
        nextWateringDate = nextWateringDate,
    )

    private fun CreatePlantRequest.validate() {
        if (name.isBlank()) throw BadRequestException("Name must not be blank")
        if (iconKey.isBlank()) throw BadRequestException("Icon key must not be blank")
        if (quantity < 1) throw BadRequestException("Quantity must be at least 1")
        when (scheduleType) {
            ScheduleType.INTERVAL -> {
                val interval = wateringIntervalDays
                    ?: throw BadRequestException("wateringIntervalDays is required for INTERVAL schedule")
                if (interval < 1) throw BadRequestException("Watering interval must be at least 1 day")
                if (firstWateringDate == null) throw BadRequestException("firstWateringDate is required for INTERVAL schedule")
            }
            ScheduleType.WEEKLY -> {
                val days = wateringDaysOfWeek
                if (days.isNullOrEmpty()) throw BadRequestException("wateringDaysOfWeek must not be empty for WEEKLY schedule")
            }
        }
    }

    private fun UpdatePlantRequest.validate() {
        name?.let { if (it.isBlank()) throw BadRequestException("Name must not be blank") }
        iconKey?.let { if (it.isBlank()) throw BadRequestException("Icon key must not be blank") }
        quantity?.let { if (it < 1) throw BadRequestException("Quantity must be at least 1") }
        wateringIntervalDays?.let { if (it < 1) throw BadRequestException("Watering interval must be at least 1 day") }
        wateringDaysOfWeek?.let {
            if (it.isEmpty()) throw BadRequestException("wateringDaysOfWeek must not be empty")
        }
    }
}
