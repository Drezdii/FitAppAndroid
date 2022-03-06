package com.bartoszdrozd.fitapp.data.workout.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "exercise_info_id") val exerciseInfoId: Int,
    val workoutId: Int
)
