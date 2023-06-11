package com.bartoszdrozd.fitapp.domain.stats

import com.bartoszdrozd.fitapp.data.stats.IStatsRepository
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.UseCase
import com.bartoszdrozd.fitapp.model.stats.BodyWeightEntry
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SaveBodyWeightEntryUseCase @Inject constructor(
    private val statsRepository: IStatsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) :
    UseCase<BodyWeightEntry, Unit>(dispatcher) {
    override suspend fun execute(params: BodyWeightEntry) {
        statsRepository.saveBodyWeightEntry(params)
    }
}