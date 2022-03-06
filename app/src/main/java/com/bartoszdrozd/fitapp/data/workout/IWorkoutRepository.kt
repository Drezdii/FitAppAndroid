package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.Result
import kotlinx.coroutines.flow.Flow

interface IWorkoutRepository {
    suspend fun getUserWorkoutsFlow(userId: String): Flow<Result<List<Workout>>>
    suspend fun getWorkout(id: Int): Flow<Result<Workout?>>
    suspend fun saveWorkout(workout: Workout): Result<Unit>
}