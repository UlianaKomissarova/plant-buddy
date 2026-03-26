package dev.uliana.plant.database

import dev.uliana.plant.model.ScheduleType
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

object PlantTable : UUIDTable("plants") {
    val userId = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 255)
    val iconKey = varchar("icon_key", 100)
    val quantity = integer("quantity").default(1)
    val scheduleType = enumerationByName<ScheduleType>("schedule_type", 20).default(ScheduleType.INTERVAL)
    val wateringIntervalDays = integer("watering_interval_days").nullable()
    val wateringDaysOfWeek = varchar("watering_days_of_week", 100).nullable()
    val nextWateringDate = date("next_watering_date")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    val deletedAt = timestamp("deleted_at").nullable()
}

class PlantEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlantEntity>(PlantTable)

    var userId by PlantTable.userId
    var name by PlantTable.name
    var iconKey by PlantTable.iconKey
    var quantity by PlantTable.quantity
    var scheduleType by PlantTable.scheduleType
    var wateringIntervalDays by PlantTable.wateringIntervalDays
    var wateringDaysOfWeek by PlantTable.wateringDaysOfWeek
    var nextWateringDate by PlantTable.nextWateringDate
    var createdAt by PlantTable.createdAt
    var updatedAt by PlantTable.updatedAt
    var deletedAt by PlantTable.deletedAt
}
