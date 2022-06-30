package com.bartoszdrozd.fitapp.data.dtos

data class ProgramCycleDTO(
    val program: ProgramDTO,
    val workoutsByWeek: Map<Int, List<WorkoutDTO>>
)