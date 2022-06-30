package com.bartoszdrozd.fitapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(tableName = "workouts")
data class WorkoutEntity constructor(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "date") val date: LocalDate?,
    @ColumnInfo(name = "start_date") val startDate: Instant?,
    @ColumnInfo(name = "end_date") val endDate: Instant?,
    @ColumnInfo(name = "type") val type: ExerciseType,
    @ColumnInfo(name = "programId") val programId: Int?,
    @ColumnInfo(name = "program_week") val programWeek: Int?
) {
    @Ignore
    var exercises: List<ExerciseEntity> = emptyList()
}
