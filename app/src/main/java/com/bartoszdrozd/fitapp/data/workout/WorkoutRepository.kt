package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.Result
import com.bartoszdrozd.fitapp.utils.toModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class WorkoutRepository @Inject constructor(
    @Named("workoutRemoteDataSource") private val remoteDataSource: IWorkoutDataSource,
    @Named("workoutLocalDataSource") private val localDataSource: IWorkoutDataSource,
    private val workoutDao: WorkoutDao
) : IWorkoutRepository {
    override suspend fun getUserWorkoutsFlow(): Flow<Result<List<Workout>>> = channelFlow {
        coroutineScope {
            launch {
                localDataSource.getUserWorkouts().collect {
                    send(Result.Success(it))
                }
            }

            launch {
                remoteDataSource.getUserWorkouts().collect {
                    localDataSource.saveWorkouts(it)
                }
            }
        }
    }

    override suspend fun getWorkout(id: Long): Flow<Result<Workout?>> = flow {
        val workout = localDataSource.getWorkout(id)

        if (workout != null && workout.exercises.isNotEmpty()) {
            // Emit from cache
            emit(Result.Success(workout))
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
    }

    override suspend fun saveWorkout(workout: Workout): Result<Unit> {
        val wrk = workoutDao.get(workout.id)
            ?: throw Exception("Couldn't save the workout. Workout not found.")

        val model = wrk.workout.toModel()

        val workoutCopy = model.copy(id = wrk.workout.serverId!!)

        val exercises = workout.exercises.map {
            val cacheExercise =
                wrk.exercises.find { ex -> ex.exercise.id == it.id }

            val serverId = cacheExercise?.exercise?.serverId ?: -1

            val exercise = it.copy(id = serverId)
            exercise.sets = it.sets.map { set ->
                val setServerId = cacheExercise?.sets?.find { s -> s.id == set.id }?.serverId ?: -1
                set.copy(id = setServerId)
            }

            exercise
        }

        workoutCopy.exercises = exercises
        remoteDataSource.saveFullWorkout(workoutCopy)

        return Result.Success(Unit)
    }
}