package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.workout.Workout

interface IWorkoutDataSource {
    suspend fun getUserWorkouts(userId: String): List<Workout>
    suspend fun getWorkout(id: Int): Workout?
    suspend fun saveWorkout(workout: Workout): Workout
}