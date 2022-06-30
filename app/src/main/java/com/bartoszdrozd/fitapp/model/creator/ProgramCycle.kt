package com.bartoszdrozd.fitapp.model.creator

import com.bartoszdrozd.fitapp.model.workout.Workout

data class ProgramCycle(val program: Program, val workoutsByWeek: Map<Int, List<Workout>>)
