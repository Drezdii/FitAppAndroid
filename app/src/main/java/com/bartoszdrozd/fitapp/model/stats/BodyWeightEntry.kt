package com.bartoszdrozd.fitapp.model.stats

import kotlinx.datetime.LocalDate

data class BodyWeightEntry(val id: Int, val date: LocalDate, val weight: Float)
