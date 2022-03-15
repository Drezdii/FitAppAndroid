package com.bartoszdrozd.fitapp.utils

import com.bartoszdrozd.fitapp.data.entities.ExerciseEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutSetEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutWithExercises
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import com.bartoszdrozd.fitapp.model.workout.WorkoutType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import kotlin.time.Duration

fun LocalDate.toWorkoutDate(): String =
    this.toJavaLocalDate().format(DateTimeFormatter.ofPattern("EEE, dd MMM"))

fun Duration.toWorkoutDuration(): String =
    this.toComponents { hours, minutes, seconds, _ ->
        return@toComponents String.format("%1\$02d:%2\$02d:%3\$02d", hours, minutes, seconds)
    }

fun WorkoutEntity.toModel(): Workout =
    Workout(id, date, startDate, endDate, WorkoutType.valueOf(type.name))

fun Workout.toEntity(): WorkoutEntity = WorkoutEntity(
    id = 0,
    serverId = id,
    date = date,
    startDate = startDate,
    endDate = endDate,
    type = type
)

fun Exercise.toEntity(workoutId: Long): ExerciseEntity =
    ExerciseEntity(id = 0, serverId = id, exerciseInfoId = exerciseInfoId, workoutId = workoutId)

fun ExerciseEntity.toModel(): Exercise = Exercise(id, exerciseInfoId)

fun WorkoutSet.toEntity(exerciseId: Long): WorkoutSetEntity =
    WorkoutSetEntity(
        id = 0,
        serverId = id,
        reps = reps,
        weight = weight,
        completed = true,
        exerciseId = exerciseId
    )

fun WorkoutSetEntity.toModel(): WorkoutSet = WorkoutSet(id, reps, weight, completed)

fun WorkoutWithExercises.toModel(): Workout =
    Workout(
        workout.id, workout.date, workout.startDate, workout.endDate, workout.type,
        exercises.map { it.exercise.toModel() }
    )