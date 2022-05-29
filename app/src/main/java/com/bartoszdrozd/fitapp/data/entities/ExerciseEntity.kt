package com.bartoszdrozd.fitapp.data.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.bartoszdrozd.fitapp.model.workout.ExerciseType

@Entity(
    tableName = "exercises",
    foreignKeys =
    [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = CASCADE
        )
    ]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "exercise_type") val exerciseType: ExerciseType,
    @ColumnInfo(name = "workout_id", index = true) val workoutId: Long,
) {
    @Ignore
    var sets: List<WorkoutSetEntity> = emptyList()
}
