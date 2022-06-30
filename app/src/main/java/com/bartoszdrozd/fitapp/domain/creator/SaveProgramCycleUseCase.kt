package com.bartoszdrozd.fitapp.domain.creator

import com.bartoszdrozd.fitapp.data.workout.IWorkoutRepository
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.UseCase
import com.bartoszdrozd.fitapp.model.creator.ProgramCycle
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SaveProgramCycleUseCase @Inject constructor(
    private val workoutRepository: IWorkoutRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UseCase<ProgramCycle, ProgramCycle>(dispatcher) {
    override suspend fun execute(params: ProgramCycle): ProgramCycle {
        return workoutRepository.saveProgramCycle(params)
    }
}