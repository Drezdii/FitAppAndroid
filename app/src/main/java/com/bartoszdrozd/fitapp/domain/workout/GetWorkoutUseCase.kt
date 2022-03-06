package com.bartoszdrozd.fitapp.domain.workout

import com.bartoszdrozd.fitapp.data.workout.IWorkoutRepository
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.FlowUseCase
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class GetWorkoutUseCase @Inject constructor(
    private val workoutRepo: IWorkoutRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Int, Workout?>(dispatcher) {
    override suspend fun execute(id: Int): Flow<Result<Workout?>> {
        return workoutRepo.getWorkout(id)
    }
}