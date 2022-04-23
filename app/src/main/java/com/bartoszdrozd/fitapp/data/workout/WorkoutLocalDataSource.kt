package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.data.entities.WorkoutEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutSetEntity
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.toEntity
import com.bartoszdrozd.fitapp.utils.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutLocalDataSource(private val workoutDao: WorkoutDao) : IWorkoutDataSource {
    override suspend fun getWorkouts(): Flow<List<Workout>> {
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

    override suspend fun saveWorkout(workout: Workout): Workout {
        val workoutEntity = workout.toEntity()

        workoutEntity.exercises = workout.exercises.map { exercise ->
            val exerciseEntity = exercise.toEntity(workoutId = workout.id)
            exerciseEntity.sets = exercise.sets.map { it.toEntity(exerciseId = exercise.id) }
            exerciseEntity
        }

        val workoutId = workoutDao.saveWorkout(workoutEntity)

        return workoutDao.getOnce(workoutId)!!.toModel()
    }

    override suspend fun saveWorkouts(workouts: List<Workout>) {
        val entities = workouts.map(Workout::toEntity)

        workoutDao.insert(entities)
    }
}