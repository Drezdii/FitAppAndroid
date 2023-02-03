package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.creator.ProgramCycle
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.ResourceNotFoundException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class WorkoutRepository @Inject constructor(
    @Named("workoutRemoteDataSource") private val remoteDataSource: IWorkoutDataSource,
    @Named("workoutLocalDataSource") private val localDataSource: IWorkoutDataSource,
) : IWorkoutRepository {
    override fun getCompletedAndActiveWorkouts(): Flow<List<Workout>> = channelFlow {
        coroutineScope {
            launch {
                localDataSource.getWorkouts()
                    // Filter to get completed and active workouts
                    .map { it.filter { workout -> (workout.startDate != null && workout.endDate != null) || (workout.endDate == null) } }
                    .collect {
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

    // TODO: Create a separate endpoint in backend that will only return planned workouts
    override fun getPlannedWorkouts(): Flow<List<Workout>> =
        getCompletedAndActiveWorkouts().map { it.filter { workout -> workout.startDate == null } }


    override fun getWorkout(id: Long): Flow<Workout> = channelFlow {
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

    override suspend fun deleteWorkout(workout: Workout) {
        remoteDataSource.deleteWorkout(workout)
        localDataSource.deleteWorkout(workout)
    }

    override suspend fun saveProgramCycle(programCycle: ProgramCycle): ProgramCycle {
        return remoteDataSource.saveProgramCycle(programCycle)
    }
}