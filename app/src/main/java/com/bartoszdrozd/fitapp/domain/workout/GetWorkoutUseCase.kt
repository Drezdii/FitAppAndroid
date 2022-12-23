package com.bartoszdrozd.fitapp.domain.workout

import com.bartoszdrozd.fitapp.data.workout.IWorkoutRepository
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.FlowUseCase
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.ResultValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class GetWorkoutUseCase @Inject constructor(
    private val workoutRepo: IWorkoutRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Long, Workout>(dispatcher) {
    override suspend fun execute(id: Long): Flow<ResultValue<Workout>> {
        if (id == 0L) {
            return flow {
                emit(ResultValue.Success(Workout()))
            }
        }
        return workoutRepo.getWorkout(id).map { ResultValue.Success(it) }
    }
}