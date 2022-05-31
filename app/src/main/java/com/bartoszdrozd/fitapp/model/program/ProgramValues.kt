package com.bartoszdrozd.fitapp.model.program

import com.bartoszdrozd.fitapp.model.workout.OneRepMax

data class ProgramValues(
    val programType: ProgramType,
    val maxes: List<OneRepMax>,
    /**
     * Value between 0-1
     */
    val trainingMax: Float
)