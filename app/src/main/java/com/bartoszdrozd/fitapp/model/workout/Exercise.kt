package com.bartoszdrozd.fitapp.model.workout

data class Exercise(
    val id: Long,
    val exerciseInfoId: Int,
    var sets: List<WorkoutSet> = emptyList()
)
