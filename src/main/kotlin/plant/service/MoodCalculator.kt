package dev.uliana.plant.service

import dev.uliana.plant.model.PlantMood
import java.time.LocalDate

object MoodCalculator {
    fun calculate(nextWateringDate: LocalDate): PlantMood =
        if (nextWateringDate >= LocalDate.now()) PlantMood.HAPPY else PlantMood.SAD
}
