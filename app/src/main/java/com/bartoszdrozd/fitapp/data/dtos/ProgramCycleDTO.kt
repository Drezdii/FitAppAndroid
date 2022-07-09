package com.bartoszdrozd.fitapp.data.dtos

data class ProgramCycleDTO(
    val workoutProgramDetails: ProgramDTO,
    val workoutsByWeek: Map<Int, List<WorkoutDTO>>
)