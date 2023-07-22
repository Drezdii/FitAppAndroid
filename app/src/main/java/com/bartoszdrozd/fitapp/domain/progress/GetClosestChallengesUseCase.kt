package com.bartoszdrozd.fitapp.domain.progress

import com.bartoszdrozd.fitapp.data.challenges.IChallengesRepository
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.FlowUseCase
import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import com.bartoszdrozd.fitapp.utils.ResultValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Get challenges that are closest to being finished
class GetClosestChallengesUseCase @Inject constructor(
    private val challengesRepo: IChallengesRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<ChallengeEntry>>(dispatcher) {
    override suspend fun execute(params: Unit): Flow<ResultValue<List<ChallengeEntry>>> =
        challengesRepo.getClosestChallenges().map { ResultValue.Success(it) }
}