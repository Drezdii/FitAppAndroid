package com.bartoszdrozd.fitapp.data.dtos

data class WorkoutSetDTO(
    val id: Long,
    var reps: Int,
    var weight: Double,
    var completed: Boolean
)