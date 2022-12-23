package com.bartoszdrozd.fitapp.domain.challenges

import com.bartoszdrozd.fitapp.data.challenges.IChallengesRepository
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.FlowUseCase
import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import com.bartoszdrozd.fitapp.utils.ResultValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChallengesUseCase @Inject constructor(
    private val challengesRepo: IChallengesRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<ChallengeEntry>>(dispatcher) {
    override suspend fun execute(params: Unit): Flow<ResultValue<List<ChallengeEntry>>> =
        challengesRepo.getChallenges().map { ResultValue.Success(it) }
}