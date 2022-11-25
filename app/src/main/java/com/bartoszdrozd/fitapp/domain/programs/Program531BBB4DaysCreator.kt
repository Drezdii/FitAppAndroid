package com.bartoszdrozd.fitapp.domain.programs

import com.bartoszdrozd.fitapp.model.program.ProgramValues
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import kotlin.math.ceil

class Program531BBB4DaysCreator {
    private val percentagesPerWeek = mapOf(
        1 to listOf(0.40, 0.50, 0.60, 0.65, 0.75, 0.85),
        2 to listOf(0.40, 0.50, 0.60, 0.70, 0.80, 0.90),
        3 to listOf(0.40, 0.50, 0.60, 0.75, 0.85, 0.95)
    )

    private val exercises = listOf(
        ExerciseLink(ExerciseType.Deadlift, ExerciseType.Squat),
        ExerciseLink(ExerciseType.Bench, ExerciseType.Ohp),
        ExerciseLink(ExerciseType.Squat, ExerciseType.Deadlift),
        ExerciseLink(ExerciseType.Ohp, ExerciseType.Bench)
    )

    fun execute(config: ProgramValues): Map<Int, List<Workout>> {
        val workoutsByWeek = mutableMapOf<Int, List<Workout>>()

        // Create 3 weeks of trainings
        for (week in 1..3) {
            val workouts = mutableListOf<Workout>()
            // With 4 workouts each week
            for (day in 0..3) {
                val workout = Workout(0, null, null, null, exercises[day].mainExercise)

                // Calculate the main lift for this day
                val exercise = Exercise(0, exercises[day].mainExercise)
                val oneRepMax = config.maxes.find { it.type == exercise.exerciseType }!!.value
                val sets = mutableListOf<WorkoutSet>()

                percentagesPerWeek[week]!!.forEachIndexed { index, percentage ->
                    val weight =
                        2.5 * ceil((percentage * (oneRepMax * config.trainingMax)) / 2.5)
                    sets.add(
                        // The last set in the warm up has only 3 reps
                        WorkoutSet(0, if (index == 2) 3 else 5, weight, false)
                    )
                }

                exercise.sets = sets
                val bbbExerciseOneRepMax =
                    config.maxes.find { it.type == exercises[day].secondaryExercise }!!.value
                val bbbExercise = Exercise(0, exercises[day].secondaryExercise)
                val bbbSets = mutableListOf<WorkoutSet>()
                // TODO: Add ability to change percentages for BBB exercises
                val bbbWeight =
                    2.5 * ceil((0.50 * (bbbExerciseOneRepMax * config.trainingMax)) / 2.5)

                for (i in 1..5) {
                    bbbSets.add(
                        WorkoutSet(0, 10, bbbWeight, false)
                    )
                }

                bbbExercise.sets = bbbSets

                workout.exercises = listOf(exercise, bbbExercise)
                workouts.add(workout)
            }

            workoutsByWeek[week] = workouts
        }

        return workoutsByWeek
    }

    private data class ExerciseLink(
        val mainExercise: ExerciseType,
        val secondaryExercise: ExerciseType
    )
}