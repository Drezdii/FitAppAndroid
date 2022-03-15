package com.bartoszdrozd.fitapp.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class ExerciseWithSets(
    @Embedded val exercise: ExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseId",
        entity = WorkoutSetEntity::class
    )
    val sets: List<WorkoutSetEntity>
)
