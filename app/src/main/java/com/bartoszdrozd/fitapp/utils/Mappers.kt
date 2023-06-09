package com.bartoszdrozd.fitapp.utils

import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.data.dtos.ChallengeDTO
import com.bartoszdrozd.fitapp.data.dtos.ChallengeEntryDTO
import com.bartoszdrozd.fitapp.data.dtos.ExerciseDTO
import com.bartoszdrozd.fitapp.data.dtos.ProgramCycleDTO
import com.bartoszdrozd.fitapp.data.dtos.ProgramDTO
import com.bartoszdrozd.fitapp.data.dtos.ProgramDetailsDTO
import com.bartoszdrozd.fitapp.data.dtos.WorkoutDTO
import com.bartoszdrozd.fitapp.data.dtos.WorkoutSetDTO
import com.bartoszdrozd.fitapp.data.entities.ExerciseEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutSetEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutWithExercises
import com.bartoszdrozd.fitapp.model.challenges.Challenge
import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import com.bartoszdrozd.fitapp.model.creator.Program
import com.bartoszdrozd.fitapp.model.creator.ProgramCycle
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import com.bartoszdrozd.fitapp.model.workout.ProgramDetails
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
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

fun Duration.toWorkoutDurationFormatted(
    hoursAbbr: String,
    minutesAbbr: String,
    secondsAbbr: String
): String =
    this.toComponents { hours, minutes, seconds, _ ->
        val builder = StringBuilder()

        // Handle an edge case where workout duration is shorter than 1 minute
        if (hours == 0L && minutes == 0) {
            return@toComponents "1$minutesAbbr"
        }

        if (hours != 0L) {
            builder.append("$hours$hoursAbbr")
        }

        builder.append(" $minutes$minutesAbbr")
        builder.append(" $seconds$secondsAbbr")

        return@toComponents builder.toString()
    }

fun WorkoutEntity.toModel(): Workout =
    Workout(
        id,
        date,
        startDate,
        endDate,
        ExerciseType.valueOf(type.name),
        workoutProgramDetails = programDetailsToModel(programId, programWeek)
    )

private fun programDetailsToModel(
    programId: Int?,
    programWeek: Int?
): ProgramDetails? {
    return if (programId != null && programWeek != null) {
        ProgramDetails(programId, programWeek)
    } else {
        null
    }
}

fun exerciseTypeToIconId(type: ExerciseType): Int {
    return when (type) {
        ExerciseType.None -> R.drawable.ic_deadlift
        ExerciseType.Deadlift -> R.drawable.ic_deadlift
        ExerciseType.Bench -> R.drawable.ic_bench_press
        ExerciseType.Squat -> R.drawable.ic_squat
        ExerciseType.Ohp -> R.drawable.ic_ohp
    }
}

fun programIdToNameId(programId: Int): Int {
    return when (programId) {
        1 -> R.string.bbb5314BBB4Days
        else -> R.string.no_exercise_name
    }
}

fun Workout.toEntity(): WorkoutEntity = WorkoutEntity(
    id = id,
    date = date,
    startDate = startDate,
    endDate = endDate,
    type = type,
    programId = workoutProgramDetails?.id,
    programWeek = workoutProgramDetails?.week
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
        exercises.map { it.exercise.toModel() },
        if (workout.programId != null && workout.programWeek != null) ProgramDetails(
            workout.programId,
            workout.programWeek
        ) else null
    )

fun WorkoutDTO.toModel(): Workout = Workout(
    id,
    date,
    startDate,
    endDate,
    type,
    exercises.map(ExerciseDTO::toModel),
    workoutProgramDetails?.toModel()
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

fun ProgramDetailsDTO.toModel(): ProgramDetails =
    ProgramDetails(id, week)

fun ChallengeEntryDTO.toModel(): ChallengeEntry =
    ChallengeEntry(value, challengeId, completedAt, challenge.toModel())

fun ChallengeDTO.toModel(): Challenge =
    Challenge(name, description, startDate, endDate, goal, unit)