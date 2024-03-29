package com.bartoszdrozd.fitapp.model.workout

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate

data class Workout(
    val id: Long = 0,
    val date: LocalDate? = java.time.LocalDate.now().toKotlinLocalDate(),
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val type: ExerciseType = ExerciseType.None,
    var exercises: List<Exercise> = emptyList(),
    val workoutProgramDetails: ProgramDetails? = null,
    val isActive: Boolean = startDate != null && endDate == null
)
