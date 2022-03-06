package com.bartoszdrozd.fitapp.model.workout

data class Exercise(
    val id: Int,
    val exerciseInfoId: Int,
    var sets: List<WorkoutSet> = emptyList()
)
