package com.bartoszdrozd.fitapp.data.dtos

import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate

data class WorkoutDTO(
    val id: Long = 0,
    val date: LocalDate? = java.time.LocalDate.now().toKotlinLocalDate(),
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val type: ExerciseType = ExerciseType.None,
    var exercises: List<ExerciseDTO> = emptyList(),
    val workoutProgramDetails: ProgramDetailsDTO? = null
)
