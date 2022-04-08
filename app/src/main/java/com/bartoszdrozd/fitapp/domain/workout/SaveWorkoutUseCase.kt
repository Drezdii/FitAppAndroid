package com.bartoszdrozd.fitapp.domain.workout

import com.bartoszdrozd.fitapp.data.workout.IWorkoutRepository
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.UseCase
import com.bartoszdrozd.fitapp.model.workout.Workout
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SaveWorkoutUseCase @Inject constructor(
    private val workoutRepository: IWorkoutRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UseCase<Workout, Long>(dispatcher) {
    override suspend fun execute(params: Workout): Long {
        return workoutRepository.saveWorkout(params)
    }
}