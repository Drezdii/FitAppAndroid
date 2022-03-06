package com.bartoszdrozd.fitapp.data.workout.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bartoszdrozd.fitapp.model.workout.WorkoutType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "start_date") val startDate: Instant?,
    @ColumnInfo(name = "end_date") val end_date: Instant?,
    @ColumnInfo(name = "type") val type: WorkoutType,
)
