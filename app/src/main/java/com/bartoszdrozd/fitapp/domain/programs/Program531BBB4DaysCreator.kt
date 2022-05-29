package com.bartoszdrozd.fitapp.domain.programs

import com.bartoszdrozd.fitapp.model.program.ProgramValues
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.ExerciseType

class Program531BBB4DaysCreator {
    private val percentagesPerWeek = mapOf(
        1 to listOf(0.40, 0.50, 0.60, 0.75, 0.80, 0.85),
        2 to listOf(0.40, 0.50, 0.60, 0.80, 0.85, 0.90),
        3 to listOf(0.40, 0.50, 0.60, 0.75, 0.85, 0.95)
    )

    private val exercises =
        listOf(ExerciseType.Deadlift, ExerciseType.Bench, ExerciseType.Squat, ExerciseType.Ohp)

    fun execute(config: ProgramValues): List<Workout> {
        val workouts = listOf<Workout>()

        // Create 3 weeks of training
        for (week in 1..3) {
            // With 4 workouts each week
            for (day in 0..3) {
                val workout = Workout(0, null, null, null)
                // Calculate the main lift for this day
            }
        }

        return workouts
    }
}