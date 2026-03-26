package dev.uliana.plant.repository

import dev.uliana.plant.database.PlantTable
import dev.uliana.plant.database.WateringLogEntity
import dev.uliana.plant.database.WateringLogTable
import dev.uliana.user.database.UserTable
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

data class CreateWateringLogDto(
    val plantId: UUID,
    val userId: UUID,
    val wateredAt: Instant,
    val scheduledDate: LocalDate,
    val note: String?,
)

interface WateringLogRepository {
    fun create(dto: CreateWateringLogDto): WateringLogEntity
    fun findByUserIdAndPlantId(userId: UUID, plantId: UUID, limit: Int = 50): List<WateringLogEntity>
}

class WateringLogRepositoryImpl : WateringLogRepository {

    override fun create(dto: CreateWateringLogDto): WateringLogEntity = transaction {
        WateringLogEntity.new {
            plantId = EntityID(dto.plantId, PlantTable)
            userId = EntityID(dto.userId, UserTable)
            wateredAt = dto.wateredAt
            scheduledDate = dto.scheduledDate
            note = dto.note
        }
    }

    override fun findByUserIdAndPlantId(userId: UUID, plantId: UUID, limit: Int): List<WateringLogEntity> =
        transaction {
            WateringLogEntity
                .find {
                    (WateringLogTable.userId eq userId) and
                            (WateringLogTable.plantId eq plantId)
                }
                .orderBy(WateringLogTable.wateredAt to SortOrder.DESC)
                .limit(limit)
                .toList()
        }
}
