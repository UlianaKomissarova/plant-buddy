package dev.uliana.util.exception

import java.time.Instant
import java.time.ZoneOffset

object DateTimeFormatter {
    private val dateTimeFormatter = java.time.format.DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withZone(ZoneOffset.UTC)

    fun instantToString(instant: Instant): String {
        return dateTimeFormatter.format(instant)
    }
}
