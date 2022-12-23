package com.bartoszdrozd.fitapp.model.challenges

import kotlinx.datetime.Instant

data class Challenge(
    val nameTranslationKey: String,
    val descriptionTranslationKey: String?,
    val startDate: Instant,
    val endDate: Instant?,
    val goal: Float,
    val unit: String?
)
