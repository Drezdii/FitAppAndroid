package com.bartoszdrozd.fitapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "sets", indices = [Index(value = ["server_id"], unique = true)])
data class WorkoutSetEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "server_id") val serverId: Long,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "completed") val completed: Boolean,
    val exerciseId: Long
)
