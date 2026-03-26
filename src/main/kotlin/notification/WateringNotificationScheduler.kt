package dev.uliana.notification

import dev.uliana.plant.repository.PlantRepository
import dev.uliana.user.repository.UserRepository
import dev.uliana.util.exception.classLogger
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.ZoneOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WateringNotificationScheduler(
    private val plantRepository: PlantRepository,
    private val userRepository: UserRepository,
) {
    private val logger = classLogger()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun start() {
        scope.launch {
            logger.info("Watering notification scheduler started")
            // Align to the next full hour before entering the loop
            delay(millisUntilNextHour())
            while (true) {
                sendNotificationsForCurrentHour()
                delay(millisUntilNextHour())
            }
        }
    }

    private fun millisUntilNextHour(): Long {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val nextHour = now.toLocalDate()
            .atTime(LocalTime.of(now.hour, 0))
            .atZone(ZoneOffset.UTC)
            .plusHours(1)
        return nextHour.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
    }

    private fun sendNotificationsForCurrentHour() {
        if (!FcmService.isEnabled()) return

        try {
            val usersWithTokens = userRepository.findAllWithFcmToken()

            usersWithTokens.forEach { user ->
                val zone = runCatching { ZoneId.of(user.timezone) }.getOrDefault(ZoneOffset.UTC)
                val userNow = ZonedDateTime.now(zone)
                val notifHour = user.notificationTime.substringBefore(":").toIntOrNull() ?: 9

                if (userNow.hour != notifHour) return@forEach

                val today = LocalDate.now(zone)
                val duePlants = plantRepository.findDueOnDateForUser(user.id.value, today)
                if (duePlants.isEmpty()) return@forEach

                val fcmToken = user.fcmToken ?: return@forEach
                val plantNames = duePlants.joinToString(", ") { it.name }
                val body = if (duePlants.size == 1) {
                    "Пора полить: $plantNames 💧"
                } else {
                    "Пора полить ${duePlants.size} растения: $plantNames 💧"
                }

                FcmService.send(fcmToken = fcmToken, title = "Полив растений", body = body)
                logger.info("Sent watering notification to user ${user.id.value} for ${duePlants.size} plants")
            }
        } catch (e: Exception) {
            logger.error("Failed to send watering notifications: ${e.message}")
        }
    }
}
