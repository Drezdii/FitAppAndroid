package com.bartoszdrozd.fitapp.data.challenges

import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import kotlinx.coroutines.flow.Flow

interface IChallengesDataSource {
    fun getChallenges(): Flow<List<ChallengeEntry>>
}