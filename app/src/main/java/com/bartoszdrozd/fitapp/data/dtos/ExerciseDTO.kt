package com.bartoszdrozd.fitapp.data.dtos

import com.bartoszdrozd.fitapp.model.workout.ExerciseType

data class ExerciseDTO(
    val id: Long,
    val exerciseType: ExerciseType,
    var sets: List<WorkoutSetDTO> = emptyList()
)