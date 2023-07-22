package com.bartoszdrozd.fitapp.model.stats

import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import kotlinx.datetime.LocalDate

data class PersonalBest(val exerciseType: ExerciseType, val value: Float, val date: LocalDate)
