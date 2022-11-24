package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.model.creator.ProgramCycle
import com.bartoszdrozd.fitapp.model.workout.Workout
import kotlinx.coroutines.flow.Flow

interface IWorkoutRepository {
    fun getCompletedAndActiveWorkouts(): Flow<List<Workout>>
    fun getPlannedWorkouts(): Flow<List<Workout>>
    fun getWorkout(id: Long): Flow<Workout>
    suspend fun saveWorkout(workout: Workout): Long
    suspend fun deleteWorkout(workout: Workout)
    suspend fun saveProgramCycle(programCycle: ProgramCycle): ProgramCycle
}