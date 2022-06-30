package com.bartoszdrozd.fitapp.utils

import com.bartoszdrozd.fitapp.data.dtos.*
import com.bartoszdrozd.fitapp.data.entities.ExerciseEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutSetEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutWithExercises
import com.bartoszdrozd.fitapp.model.creator.Program
import com.bartoszdrozd.fitapp.model.creator.ProgramCycle
import com.bartoszdrozd.fitapp.model.workout.*
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
    Workout(
        id,
        date,
        startDate,
        endDate,
        ExerciseType.valueOf(type.name),
        program = ProgramDetails(programId, null, programWeek)
    )

fun Workout.toEntity(): WorkoutEntity = WorkoutEntity(
    id = id,
    date = date,
    startDate = startDate,
    endDate = endDate,
    type = type,
    programId = program?.id,
    programWeek = program?.week
)

fun Exercise.toEntity(workoutId: Long): ExerciseEntity =
    ExerciseEntity(id = id, exerciseType = exerciseType, workoutId = workoutId)

fun ExerciseEntity.toModel(): Exercise = Exercise(id, exerciseType)

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
    id,
    date,
    startDate,
    endDate,
    type,
    exercises.map(ExerciseDTO::toModel),
    program?.toModel()
)

fun ExerciseDTO.toModel(): Exercise =
    Exercise(id, exerciseType, sets.map(WorkoutSetDTO::toModel))

fun WorkoutSetDTO.toModel(): WorkoutSet = WorkoutSet(id, reps, weight, completed)

fun Workout.toDTO(): WorkoutDTO = WorkoutDTO(
    id, date, startDate, endDate, type, exercises.map(Exercise::toDTO)
)

fun Exercise.toDTO(): ExerciseDTO =
    ExerciseDTO(id, exerciseType, sets.map(WorkoutSet::toDTO))

fun WorkoutSet.toDTO(): WorkoutSetDTO = WorkoutSetDTO(id, reps, weight, completed)

fun ProgramCycle.toDTO(): ProgramCycleDTO = ProgramCycleDTO(
    program.toDTO(),
    // Perform mapping of Workout objects to WorkoutDTO objects in the workout map
    workoutsByWeek.map { it.key to it.value.map { workout -> workout.toDTO() } }.toMap()
)

fun Program.toDTO(): ProgramDTO = ProgramDTO(id, name)

fun ProgramDetailsDTO.toModel(): ProgramDetails = ProgramDetails(id, name, week)
