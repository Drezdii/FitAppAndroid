package com.bartoszdrozd.fitapp.data.entities

import androidx.room.*
import com.bartoszdrozd.fitapp.model.workout.WorkoutType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(tableName = "workouts", indices = [Index(value = ["server_id"], unique = true)])
data class WorkoutEntity constructor(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "server_id") val serverId: Long?,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "start_date") val startDate: Instant?,
    @ColumnInfo(name = "end_date") val endDate: Instant?,
    @ColumnInfo(name = "type") val type: WorkoutType,
) {
    @Ignore
    var exercises: List<ExerciseEntity> = emptyList()
}
