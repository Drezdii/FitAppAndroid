package com.bartoszdrozd.fitapp.data.dtos

import kotlinx.datetime.LocalDate

data class BodyWeightEntryDTO(val id: Int, val date: LocalDate, val weight: Float)
