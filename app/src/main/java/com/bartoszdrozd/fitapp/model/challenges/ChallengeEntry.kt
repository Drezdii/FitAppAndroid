package com.bartoszdrozd.fitapp.model.challenges

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class ChallengeEntry(
    val value: Float,
    val challengeId: String,
    val completedAt: LocalDate?,
    val challenge: Challenge
)
