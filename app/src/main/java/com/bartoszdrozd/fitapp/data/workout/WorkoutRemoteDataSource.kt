package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.data.auth.IUserRepository
import com.bartoszdrozd.fitapp.data.dtos.WorkoutDTO
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.toDTO
import com.bartoszdrozd.fitapp.utils.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WorkoutRemoteDataSource @Inject constructor(
    private val workoutService: IWorkoutService,
    private val userRepository: IUserRepository
) : IWorkoutDataSource {
    override suspend fun getWorkouts(): Flow<List<Workout>> = flow {
        // TODO: Do something about userId possibly being null
        val res = workoutService.getWorkouts(userRepository.getUserId() ?: "")

        if (res.isSuccessful) {
            emit(res.body()!!.map(WorkoutDTO::toModel))
        } else {
            // Emit error loading workouts from the server
        }
    }


    override suspend fun getWorkout(id: Long): Flow<Workout?> = flow {
        val res = workoutService.getWorkout(id)

        if (res.isSuccessful) {
            emit(res.body()?.toModel())
        } else {
            // Emit error loading workout from the server
        }
    }


    override suspend fun saveWorkout(workout: Workout): Workout {
        val res = workoutService.saveWorkout(workout.toDTO())

        if (res.isSuccessful) {
            return res.body()!!.toModel()
        } else {
            throw Exception()
            // Throw an error
        }
    }

    override suspend fun saveWorkouts(workouts: List<Workout>) {
        TODO("Not yet implemented")
    }
}