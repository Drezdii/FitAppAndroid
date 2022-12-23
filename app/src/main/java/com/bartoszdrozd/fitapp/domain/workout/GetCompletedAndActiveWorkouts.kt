package com.bartoszdrozd.fitapp.domain.workout

import com.bartoszdrozd.fitapp.data.workout.IWorkoutRepository
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.FlowUseCase
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.ResultValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCompletedAndActiveWorkouts @Inject constructor(
    private val workoutRepo: IWorkoutRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<Workout>>(dispatcher) {
    override suspend fun execute(params: Unit): Flow<ResultValue<List<Workout>>> {
        return workoutRepo.getCompletedAndActiveWorkouts().map { ResultValue.Success(it) }
    }
}