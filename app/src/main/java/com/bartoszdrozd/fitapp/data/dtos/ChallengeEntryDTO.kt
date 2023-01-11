package com.bartoszdrozd.fitapp.data.dtos

import kotlinx.datetime.LocalDate

data class ChallengeEntryDTO(
    val value: Float,
    val challengeId: String,
    val completedAt: LocalDate?,
    val challenge: ChallengeDTO
)

