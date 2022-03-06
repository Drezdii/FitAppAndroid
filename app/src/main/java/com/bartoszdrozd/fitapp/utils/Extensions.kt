package com.bartoszdrozd.fitapp.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import kotlin.time.Duration

fun LocalDate.toWorkoutDate(): String =
    this.toJavaLocalDate().format(DateTimeFormatter.ofPattern("EEE, dd MMM"))

fun Duration.toWorkoutDuration(): String =
    this.toComponents { hours, minutes, seconds, _ ->
        return@toComponents String.format("%1\$02d:%2\$02d:%3\$02d", hours, minutes, seconds)
    }