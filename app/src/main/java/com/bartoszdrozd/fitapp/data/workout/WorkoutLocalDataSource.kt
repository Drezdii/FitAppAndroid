package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.data.entities.WorkoutEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutSetEntity
import com.bartoszdrozd.fitapp.model.workout.Workout
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

    override suspend fun getWorkout(id: Long): Workout? {
        val workout = workoutDao.get(id) ?: return null

        val exercises = workout.exercises.map {
            val exercise = it.exercise.toModel()
            exercise.sets = it.sets.map(WorkoutSetEntity::toModel)

            exercise
        }

        val wrk = workout.toModel()
        wrk.exercises = exercises

        return wrk
    }

    override suspend fun saveFullWorkout(workout: Workout): Workout {
        val exercises = workout.exercises.map {
            it.toEntity(workout.id)
        }

        val sets = workout.exercises.flatMap { exercise ->
            exercise.sets.map { it.toEntity(exercise.id) }
        }


        val res = workoutDao.saveWorkout(workout.toEntity(), exercises, sets = sets)
        return workoutDao.get(res)!!.workout.toModel()
    }

    override suspend fun saveWorkouts(workouts: List<Workout>) {
        val entities = workouts.map(Workout::toEntity)

        workoutDao.insertWorkouts(entities)
    }
}