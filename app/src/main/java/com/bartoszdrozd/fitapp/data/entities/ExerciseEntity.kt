package com.bartoszdrozd.fitapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "exercises", indices = [Index(value = ["server_id"], unique = true)])
data class ExerciseEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "server_id") val serverId: Long,
    @ColumnInfo(name = "exercise_info_id") val exerciseInfoId: Int,
    val workoutId: Long
)
