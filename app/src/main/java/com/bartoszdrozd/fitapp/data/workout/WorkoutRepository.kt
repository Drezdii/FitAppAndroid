package com.bartoszdrozd.fitapp.data.workout

import android.util.Log
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.Result
import com.bartoszdrozd.fitapp.utils.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Named

class WorkoutRepository @Inject constructor(
    @Named("workoutRemoteDataSource") private val remoteDataSource: IWorkoutDataSource,
    @Named("workoutLocalDataSource") private val localDataSource: IWorkoutDataSource,
    private val workoutDao: WorkoutDao
) : IWorkoutRepository {
    override suspend fun getUserWorkoutsFlow(): Flow<Result<List<Workout>>> = flow {
//        val token = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
//        Log.d("TEST", token.toString())
        // TODO: Emit data from cache
        emit(Result.Success(emptyList()))
        emit(Result.Loading)
        remoteDataSource.getUserWorkouts().collect {
            localDataSource.saveWorkouts(it)
        }

        localDataSource.getUserWorkouts().collect {
            emit(Result.Success(it))
        }
    }

    override suspend fun getWorkout(id: Long): Flow<Result<Workout?>> = flow {
        // TODO: Emit data from cache
        emit(Result.Success(null))
        emit(Result.Loading)

        val workout = workoutDao.get(id)

        if (workout != null) {
            // Emit from cache
            emit(Result.Success(workout.toModel()))
        } else {
            // Get the workout from the server
            // Map id to server-side ID
            val realId = workoutDao.getRealId(id)
                ?: throw IllegalArgumentException("Couldn't find a workout with provided ID.")

            val workoutRemote = remoteDataSource.getWorkout(realId)

            if (workoutRemote != null) {
                val res = localDataSource.saveFullWorkout(workoutRemote)
                emit(Result.Success(localDataSource.getWorkout(res.id)))
            } else {
                emit(Result.Error(IllegalArgumentException("Couldn't find a workout with provided server-side ID.")))
            }
        }
//        if (workout != null) {
////            val res = localDataSource.saveFullWorkout(workout)
//            localDataSource.getWorkout(res.id)
//            emit(Result.Success(workout))
//        } else {
//            emit(Result.Error(IllegalArgumentException("Couldn't find a workout with provided server-side ID.")))
//        }
    }

    override suspend fun saveWorkout(workout: Workout): Result<Unit> {
        // Find server-side Id of workout
        val realId = workoutDao.getRealId(workout.id) ?: return Result.Error(
            IllegalArgumentException("Couldn't find a workout with provided ID.")
        )

        // Change client-side ID to server-side ID
        val workoutCopy = workout.copy(id = realId)

        remoteDataSource.saveFullWorkout(workoutCopy)
        Log.d("TEST", "Saved")
        return Result.Success(Unit)
    }
}