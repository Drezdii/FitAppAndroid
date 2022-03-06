package com.bartoszdrozd.fitapp.data.workout.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sets")
data class WorkoutSetEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "completed") val completed: Boolean,
    val exerciseId: Int
)
