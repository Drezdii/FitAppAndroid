package com.bartoszdrozd.fitapp.model.workout

data class WorkoutSet(
    val id: Int,
    var reps: Int,
    var weight: Double,
    var completed: Boolean
)