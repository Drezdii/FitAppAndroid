package com.bartoszdrozd.fitapp.data.dtos

import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import kotlinx.datetime.LocalDate

data class PersonalBestDTO(val exerciseType: ExerciseType, val value: Float, val date: LocalDate)
