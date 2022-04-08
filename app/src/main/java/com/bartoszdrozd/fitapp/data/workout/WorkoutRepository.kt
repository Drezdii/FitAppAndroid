package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.Result
import com.bartoszdrozd.fitapp.utils.toModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class WorkoutRepository @Inject constructor(
    @Named("workoutRemoteDataSource") private val remoteDataSource: IWorkoutDataSource,
    @Named("workoutLocalDataSource") private val localDataSource: IWorkoutDataSource,
    private val workoutDao: WorkoutDao
) : IWorkoutRepository {
    override suspend fun getUserWorkouts(): Flow<Result<List<Workout>>> = channelFlow {
        coroutineScope {
            launch {
                localDataSource.getUserWorkouts().collect {
                    send(Result.Success(it))
                }
            }

            launch {
                remoteDataSource.getUserWorkouts().collect {
                    localDataSource.saveRemoteWorkouts(it)
                }
            }
        }
    }

    override suspend fun getWorkout(id: Long): Flow<Result<Workout?>> = channelFlow {
        coroutineScope {
            launch {
                localDataSource.getWorkout(id).collect {
                    send(Result.Success(it))
                }
            }

            launch {
                val realId = workoutDao.getRealId(id)
                    ?: return@launch

                remoteDataSource.getWorkout(realId).collect {
                    if (it != null) {
                        localDataSource.saveFullWorkout(it)
                    } else {
                        send(Result.Error(IllegalArgumentException("Couldn't find a workout with provided server-side ID. $realId")))
                    }
                }
            }
        }
    }

    override suspend fun saveWorkout(workout: Workout): Long {
        if (workout.id == 0L) {
            val res = remoteDataSource.saveFullWorkout(workout)
            val localWorkout = localDataSource.saveFullWorkout(res)
            return localWorkout.id
        }

        val wrk = workoutDao.getOnce(workout.id)
            ?: throw Exception("Couldn't save the workout. Workout not found.")

        val model = wrk.workout.toModel()

        val workoutCopy = model.copy(
            id = wrk.workout.serverId!!,
            startDate = workout.startDate,
            endDate = workout.endDate,
            type = workout.type
        )

        val exercises = workout.exercises.map {
            val cacheExercise =
                wrk.exercises.find { ex -> ex.exercise.id == it.id }

            val serverId = cacheExercise?.exercise?.serverId ?: -1

            val exercise = it.copy(id = serverId)
            exercise.sets = it.sets.map { set ->
                val setServerId =
                    cacheExercise?.sets?.find { s -> s.id == set.id }?.serverId ?: -1
                set.copy(id = setServerId)
            }

            exercise
        }

        workoutCopy.exercises = exercises
        val res = remoteDataSource.saveFullWorkout(workoutCopy)
        localDataSource.saveFullWorkout(res)

        return workout.id
    }
}