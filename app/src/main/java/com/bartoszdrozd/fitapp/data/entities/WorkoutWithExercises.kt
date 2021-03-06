package com.bartoszdrozd.fitapp.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "workout_id",
        entity = ExerciseEntity::class
    )
    val exercises: List<ExerciseWithSets>
)