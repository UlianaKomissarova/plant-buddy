package dev.uliana.plant.database

import dev.uliana.user.database.UserTable
import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object WateringLogTable : UUIDTable("watering_logs") {
    val plantId = reference("plant_id", PlantTable, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val wateredAt = timestamp("watered_at")
    val scheduledDate = date("scheduled_date")
    val note = text("note").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}

class WateringLogEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<WateringLogEntity>(WateringLogTable)

    var plantId by WateringLogTable.plantId
    var userId by WateringLogTable.userId
    var wateredAt by WateringLogTable.wateredAt
    var scheduledDate by WateringLogTable.scheduledDate
    var note by WateringLogTable.note
    var createdAt by WateringLogTable.createdAt
}
