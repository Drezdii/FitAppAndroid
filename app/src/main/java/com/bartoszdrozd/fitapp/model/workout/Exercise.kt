package com.bartoszdrozd.fitapp.model.workout

data class Exercise(
    val id: Long,
    val exerciseType: ExerciseType,
    var sets: List<WorkoutSet> = emptyList()
)
