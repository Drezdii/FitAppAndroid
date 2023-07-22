package com.bartoszdrozd.fitapp.data.challenges

import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import com.bartoszdrozd.fitapp.model.stats.PersonalBest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChallengesRepository @Inject constructor(
    private val remoteDataSource: IChallengesDataSource
) : IChallengesRepository {
    override fun getChallenges(): Flow<List<ChallengeEntry>> = remoteDataSource.getChallenges()
    override fun getPersonalBests(): Flow<List<PersonalBest>> = remoteDataSource.getPersonalBests()
    override fun getClosestChallenges(): Flow<List<ChallengeEntry>> = remoteDataSource.getClosestChallenges()
}