package com.bartoszdrozd.fitapp.data.dtos

data class ExerciseDTO(
    val id: Long,
    val exerciseInfoId: Int,
    var sets: List<WorkoutSetDTO> = emptyList()
)