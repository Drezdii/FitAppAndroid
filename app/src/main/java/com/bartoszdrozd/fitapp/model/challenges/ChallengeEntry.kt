package com.bartoszdrozd.fitapp.model.challenges

import kotlinx.datetime.Instant

data class ChallengeEntry(
    val value: Float,
    val challengeId: String,
    val completedAt: Instant?,
    val challenge: Challenge
)
