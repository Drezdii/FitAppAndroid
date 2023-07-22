package com.bartoszdrozd.fitapp.model.program

import com.bartoszdrozd.fitapp.model.stats.PersonalBest

data class ProgramValues(
    val programType: ProgramType,
    val maxes: List<PersonalBest>,
    /**
     * Value between 0-1
     */
    val trainingMax: Float
)