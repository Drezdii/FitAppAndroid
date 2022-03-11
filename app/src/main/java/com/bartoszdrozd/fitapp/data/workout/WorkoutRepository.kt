package com.bartoszdrozd.fitapp.data.workout

import android.util.Log
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class WorkoutRepository @Inject constructor(
    @Named("workoutRemoteDataSource") private val remoteDataSource: IWorkoutDataSource
) : IWorkoutRepository {
    override suspend fun getUserWorkoutsFlow(userId: String): Flow<Result<List<Workout>>> = flow {
        val token = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
        Log.d("TEST", token.toString())
        // TODO: Emit data from cache
        emit(Result.Success(emptyList()))
        emit(Result.Loading)
        emit(Result.Success(remoteDataSource.getUserWorkouts(userId)))
    }

    override suspend fun getWorkout(id: Int): Flow<Result<Workout?>> = flow {
        // TODO: Emit data from cache
        emit(Result.Success(null))
        emit(Result.Loading)
        emit(Result.Success(remoteDataSource.getWorkout(id)))
    }

    override suspend fun saveWorkout(workout: Workout): Result<Unit> {
        remoteDataSource.saveWorkout(workout)

        return Result.Success(Unit)
    }
}