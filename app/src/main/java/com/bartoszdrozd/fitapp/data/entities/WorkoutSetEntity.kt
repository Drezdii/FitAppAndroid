package com.bartoszdrozd.fitapp.data.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "sets", indices = [Index(value = ["server_id"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = CASCADE
        )
    ]
)
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "server_id") val serverId: Long,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "completed") val completed: Boolean,
    @ColumnInfo(name = "exercise_id", index = true) val exerciseId: Long
)
