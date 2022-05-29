package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.ResourceNotFoundException
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
    override suspend fun getUserWorkouts(): Flow<List<Workout>> = channelFlow {
        coroutineScope {
            launch {
                localDataSource.getWorkouts().collect {
                    send(it)
                }
            }

            launch {
                remoteDataSource.getWorkouts().collect {
                    localDataSource.saveWorkouts(it)
                }
            }
        }
    }

    override suspend fun getWorkout(id: Long): Flow<Workout> = channelFlow {
        coroutineScope {
            launch {
                localDataSource.getWorkout(id).collect {
                    if (it != null) {
                        send(it)
                    }
                }
            }

            launch {
                remoteDataSource.getWorkout(id).collect {
                    if (it != null) {
                        localDataSource.saveWorkout(it)
                    } else {
                        throw ResourceNotFoundException("Error getting workout with ID: $id")
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