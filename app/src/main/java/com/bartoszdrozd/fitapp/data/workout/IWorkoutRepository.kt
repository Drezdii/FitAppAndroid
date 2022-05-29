package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.workout.Workout
import kotlinx.coroutines.flow.Flow

interface IWorkoutRepository {
    suspend fun getUserWorkouts(): Flow<List<Workout>>
    suspend fun getWorkout(id: Long): Flow<Workout>
    suspend fun saveWorkout(workout: Workout): Long
}