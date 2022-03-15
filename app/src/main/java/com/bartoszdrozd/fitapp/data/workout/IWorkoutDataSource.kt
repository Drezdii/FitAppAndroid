package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.workout.Workout
import kotlinx.coroutines.flow.Flow

interface IWorkoutDataSource {
    suspend fun getUserWorkouts(): Flow<List<Workout>>
    suspend fun getWorkout(id: Long): Workout?
    suspend fun saveFullWorkout(workout: Workout): Workout
    suspend fun saveWorkouts(workouts: List<Workout>)
}