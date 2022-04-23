package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.workout.Workout
import kotlinx.coroutines.flow.Flow

interface IWorkoutDataSource {
    suspend fun getWorkouts(): Flow<List<Workout>>
    suspend fun getWorkout(id: Long): Flow<Workout?>
    suspend fun saveWorkout(workout: Workout): Workout
    suspend fun saveWorkouts(workouts: List<Workout>)
}