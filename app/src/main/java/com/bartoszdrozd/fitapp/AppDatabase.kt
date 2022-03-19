package com.bartoszdrozd.fitapp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bartoszdrozd.fitapp.data.entities.ExerciseEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutSetEntity
import com.bartoszdrozd.fitapp.data.workout.WorkoutDao
import com.bartoszdrozd.fitapp.utils.RoomTypeConverters

@Database(
    entities = [WorkoutEntity::class, ExerciseEntity::class, WorkoutSetEntity::class],
    version = 13
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}