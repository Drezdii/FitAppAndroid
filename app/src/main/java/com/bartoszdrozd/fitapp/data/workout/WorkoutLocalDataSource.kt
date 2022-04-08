package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.data.entities.WorkoutEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutSetEntity
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import com.bartoszdrozd.fitapp.utils.toEntity
import com.bartoszdrozd.fitapp.utils.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutLocalDataSource(private val workoutDao: WorkoutDao) : IWorkoutDataSource {
    override suspend fun getUserWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAll().map {
            it.map(WorkoutEntity::toModel)
        }
    }

    override suspend fun getWorkout(id: Long): Flow<Workout?> {
        return workoutDao.get(id).map { workout ->
            if (workout == null) {
                return@map null
            }

            val exercises = workout.exercises.map {
                val exercise = it.exercise.toModel()
                exercise.sets = it.sets.map(WorkoutSetEntity::toModel)

                exercise
            }

            val wrk = workout.toModel()
            wrk.exercises = exercises
            wrk
        }
    }

    override suspend fun saveFullWorkout(workout: Workout): Workout {
        val workoutEntity = workout.toEntity()
        workoutEntity.exercises = workout.exercises.map {
            val exerciseEntity = it.toEntity()
            exerciseEntity.sets = it.sets.map(WorkoutSet::toEntity)
            exerciseEntity
        }

        val workoutId = workoutDao.saveWorkout(workoutEntity)

        return workoutDao.getOnce(workoutId)!!.toModel()
    }

    override suspend fun saveRemoteWorkouts(workouts: List<Workout>) {
        val entities = workouts.map(Workout::toEntity)

        workoutDao.insertRemoteWorkouts(entities)
    }
}