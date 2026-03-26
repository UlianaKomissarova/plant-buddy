package dev.uliana.plant.model

enum class ScheduleType {
    /** Every N days from the last watering date. */
    INTERVAL,

    /** Specific days of the week (e.g. Monday and Thursday). */
    WEEKLY,
}
