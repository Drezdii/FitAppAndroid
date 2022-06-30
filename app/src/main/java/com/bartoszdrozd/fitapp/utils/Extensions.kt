package com.bartoszdrozd.fitapp.utils

import androidx.compose.ui.Modifier
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.workout.ExerciseType

fun Modifier.modifyIf(condition: Boolean, modify: Modifier.() -> Modifier) =
    if (condition) modify() else this

fun ExerciseType.toNameResId(): Int {
    return when (this) {
        ExerciseType.Deadlift -> R.string.deadlift
        ExerciseType.Bench -> R.string.bench
        ExerciseType.Squat -> R.string.squat
        ExerciseType.Ohp -> R.string.ohp
        else -> R.string.no_exercise_name
    }
}