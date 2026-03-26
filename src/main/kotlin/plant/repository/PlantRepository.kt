package dev.uliana.plant.repository

import dev.uliana.plant.database.PlantEntity
import dev.uliana.plant.database.PlantTable
import dev.uliana.plant.model.ScheduleType
import dev.uliana.user.database.UserTable
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

data class CreatePlantDto(
    val userId: UUID,
    val name: String,
    val iconKey: String,
    val quantity: Int,
    val scheduleType: ScheduleType,
    val wateringIntervalDays: Int?,
    val wateringDaysOfWeek: String?,
    val nextWateringDate: LocalDate,
)

data class UpdatePlantDto(
    val name: String?,
    val iconKey: String?,
    val quantity: Int?,
    val scheduleType: ScheduleType?,
    val wateringIntervalDays: Int?,
    val wateringDaysOfWeek: String?,
    val nextWateringDate: LocalDate?,
)

interface PlantRepository {
    fun create(dto: CreatePlantDto): PlantEntity
    fun findById(id: UUID): PlantEntity?
    fun findAllByUserId(userId: UUID): List<PlantEntity>
    fun findAllDueOnDate(date: LocalDate): List<PlantEntity>
    fun findDueOnDateForUser(userId: UUID, date: LocalDate): List<PlantEntity>
    fun update(id: UUID, dto: UpdatePlantDto): PlantEntity?
    fun updateNextWateringDate(id: UUID, date: LocalDate)
    fun softDelete(id: UUID)
}

class PlantRepositoryImpl : PlantRepository {

    override fun create(dto: CreatePlantDto): PlantEntity = transaction {
        PlantEntity.new {
            userId = EntityID(dto.userId, UserTable)
            name = dto.name
            iconKey = dto.iconKey
            quantity = dto.quantity
            scheduleType = dto.scheduleType
            wateringIntervalDays = dto.wateringIntervalDays
            wateringDaysOfWeek = dto.wateringDaysOfWeek
            nextWateringDate = dto.nextWateringDate
        }
    }

    override fun findById(id: UUID): PlantEntity? = transaction {
        PlantEntity.find {
            (PlantTable.id eq id) and PlantTable.deletedAt.isNull()
        }.firstOrNull()
    }

    override fun findAllByUserId(userId: UUID): List<PlantEntity> = transaction {
        PlantEntity.find {
            (PlantTable.userId eq userId) and PlantTable.deletedAt.isNull()
        }.toList()
    }

    override fun findAllDueOnDate(date: LocalDate): List<PlantEntity> = transaction {
        PlantEntity.find {
            (PlantTable.nextWateringDate lessEq date) and PlantTable.deletedAt.isNull()
        }.toList()
    }

    override fun findDueOnDateForUser(userId: UUID, date: LocalDate): List<PlantEntity> = transaction {
        PlantEntity.find {
            (PlantTable.userId eq userId) and
                (PlantTable.nextWateringDate lessEq date) and
                PlantTable.deletedAt.isNull()
        }.toList()
    }

    override fun update(id: UUID, dto: UpdatePlantDto): PlantEntity? = transaction {
        PlantEntity.findByIdAndUpdate(id) { plant ->
            dto.name?.let { plant.name = it }
            dto.iconKey?.let { plant.iconKey = it }
            dto.quantity?.let { plant.quantity = it }
            dto.scheduleType?.let { plant.scheduleType = it }
            dto.wateringIntervalDays?.let { plant.wateringIntervalDays = it }
            dto.wateringDaysOfWeek?.let { plant.wateringDaysOfWeek = it }
            dto.nextWateringDate?.let { plant.nextWateringDate = it }
            plant.updatedAt = Instant.now()
        }
    }

    override fun updateNextWateringDate(id: UUID, date: LocalDate): Unit = transaction {
        PlantEntity.findByIdAndUpdate(id) {
            it.nextWateringDate = date
            it.updatedAt = Instant.now()
        }
    }

    override fun softDelete(id: UUID): Unit = transaction {
        PlantEntity.findByIdAndUpdate(id) {
            it.deletedAt = Instant.now()
        }
    }
}
