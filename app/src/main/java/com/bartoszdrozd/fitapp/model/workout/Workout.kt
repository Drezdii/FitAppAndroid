package com.bartoszdrozd.fitapp.model.workout

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate

data class Workout(
    val id: Long = 0,
    val date: LocalDate = java.time.LocalDate.now().toKotlinLocalDate(),
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val type: WorkoutType = WorkoutType.NONE,
    var exercises: List<Exercise> = emptyList()
)
