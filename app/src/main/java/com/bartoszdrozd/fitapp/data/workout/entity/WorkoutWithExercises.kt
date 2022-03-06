package com.bartoszdrozd.fitapp.data.workout.entity

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId",
        entity = ExerciseEntity::class
    )
    val exercises: List<ExerciseWithSets>
)