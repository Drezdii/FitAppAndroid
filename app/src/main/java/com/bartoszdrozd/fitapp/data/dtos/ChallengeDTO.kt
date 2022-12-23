package com.bartoszdrozd.fitapp.data.dtos

import kotlinx.datetime.Instant

data class ChallengeDTO(
    val nameTranslationKey: String,
    val descriptionTranslationKey: String?,
    val startDate: Instant,
    val endDate: Instant?,
    val goal: Float,
    val unit: String?
)
