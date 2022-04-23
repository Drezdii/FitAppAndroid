package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.ResourceNotFoundException
import com.bartoszdrozd.fitapp.utils.Result
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class WorkoutRepository @Inject constructor(
    @Named("workoutRemoteDataSource") private val remoteDataSource: IWorkoutDataSource,
    @Named("workoutLocalDataSource") private val localDataSource: IWorkoutDataSource,
) : IWorkoutRepository {
    override suspend fun getUserWorkouts(): Flow<Result<List<Workout>>> = channelFlow {
        coroutineScope {
            launch {
                localDataSource.getWorkouts().collect {
                    send(Result.Success(it))
                }
            }

            launch {
                remoteDataSource.getWorkouts().collect {
                    localDataSource.saveWorkouts(it)
                }
            }
        }
    }

    override suspend fun getWorkout(id: Long): Flow<Result<Workout>> = channelFlow {
        coroutineScope {
            launch {
                localDataSource.getWorkout(id).collect {
                    if (it != null) {
                        send(Result.Success(it))
                    }
                }
            }

            launch {
                remoteDataSource.getWorkout(id).collect {
                    if (it != null) {
                        localDataSource.saveWorkout(it)
                    } else {
                        send(Result.Error(ResourceNotFoundException("Error getting workout with ID: $id")))
                    }
                }
            }
        }
    }

    override suspend fun saveWorkout(workout: Workout): Long {
        val res = remoteDataSource.saveWorkout(workout)
        val localWorkout = localDataSource.saveWorkout(res)

        return localWorkout.id
    }
}