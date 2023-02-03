package com.bartoszdrozd.fitapp.data.dtos

import kotlinx.datetime.Instant

data class ChallengeDTO(
    val name: String,
    val description: String?,
    val startDate: Instant,
    val endDate: Instant?,
    val goal: Float,
    val unit: String?
)
