package com.bartoszdrozd.fitapp.data.dtos

import kotlinx.datetime.Instant

data class ChallengeEntryDTO(
    val value: Float,
    val challengeId: String,
    val completedAt: Instant?,
    val challenge: ChallengeDTO
)

