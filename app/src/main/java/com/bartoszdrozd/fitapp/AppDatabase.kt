package com.bartoszdrozd.fitapp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bartoszdrozd.fitapp.data.workout.entity.ExerciseEntity
import com.bartoszdrozd.fitapp.data.workout.entity.WorkoutEntity
import com.bartoszdrozd.fitapp.data.workout.entity.WorkoutSetEntity
import com.bartoszdrozd.fitapp.utils.RoomTypeConverters

@Database(
    entities = [WorkoutEntity::class, ExerciseEntity::class, WorkoutSetEntity::class],
    version = 1
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
}