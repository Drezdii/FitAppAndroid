package com.bartoszdrozd.fitapp.domain.creator

import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.UseCase
import com.bartoszdrozd.fitapp.domain.programs.Program531BBB4DaysCreator
import com.bartoszdrozd.fitapp.model.program.ProgramType.BBB_531_4_Days
import com.bartoszdrozd.fitapp.model.program.ProgramValues
import com.bartoszdrozd.fitapp.model.workout.Workout
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class CreateProgramUseCase @Inject constructor(
    private val program531BBB4Days: Program531BBB4DaysCreator,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UseCase<ProgramValues, Map<Int, List<Workout>>>(dispatcher) {
    override suspend fun execute(params: ProgramValues): Map<Int, List<Workout>> {
        return when (params.programType) {
            BBB_531_4_Days -> program531BBB4Days.execute(params)
        }
    }
}