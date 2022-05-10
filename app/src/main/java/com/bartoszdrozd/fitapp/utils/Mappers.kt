package com.bartoszdrozd.fitapp.utils

import com.bartoszdrozd.fitapp.data.dtos.ExerciseDTO
import com.bartoszdrozd.fitapp.data.dtos.WorkoutDTO
import com.bartoszdrozd.fitapp.data.dtos.WorkoutSetDTO
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
    id = id,
    date = date,
    startDate = startDate,
    endDate = endDate,
    type = type
)

fun Exercise.toEntity(workoutId: Long): ExerciseEntity =
    ExerciseEntity(id = id, exerciseInfoId = exerciseInfoId, workoutId = workoutId)

fun ExerciseEntity.toModel(): Exercise = Exercise(id, exerciseInfoId)

fun WorkoutSet.toEntity(exerciseId: Long): WorkoutSetEntity =
    WorkoutSetEntity(
        id = id,
        reps = reps,
        weight = weight,
        completed = completed,
        exerciseId = exerciseId
    )

fun WorkoutSetEntity.toModel(): WorkoutSet = WorkoutSet(id, reps, weight, completed)

fun WorkoutWithExercises.toModel(): Workout =
    Workout(
        workout.id, workout.date, workout.startDate, workout.endDate, workout.type,
        exercises.map { it.exercise.toModel() }
    )

fun WorkoutDTO.toModel(): Workout = Workout(
    id, date, startDate, endDate, type, exercises = exercises.map(ExerciseDTO::toModel)
)

fun ExerciseDTO.toModel(): Exercise =
    Exercise(id, exerciseInfoId, sets = sets.map(WorkoutSetDTO::toModel))

fun WorkoutSetDTO.toModel(): WorkoutSet = WorkoutSet(id, reps, weight, completed)

fun Workout.toDTO(): WorkoutDTO = WorkoutDTO(
    id, date, startDate, endDate, type, exercises = exercises.map(Exercise::toDTO)
)

fun Exercise.toDTO(): ExerciseDTO =
    ExerciseDTO(id, exerciseInfoId, sets = sets.map(WorkoutSet::toDTO))

fun WorkoutSet.toDTO(): WorkoutSetDTO = WorkoutSetDTO(id, reps, weight, completed)
