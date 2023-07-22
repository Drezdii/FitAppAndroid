package com.bartoszdrozd.fitapp.data.challenges

import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import com.bartoszdrozd.fitapp.model.stats.PersonalBest
import kotlinx.coroutines.flow.Flow

interface IChallengesRepository {
    fun getChallenges(): Flow<List<ChallengeEntry>>
    fun getPersonalBests(): Flow<List<PersonalBest>>
    fun getClosestChallenges(): Flow<List<ChallengeEntry>>
}