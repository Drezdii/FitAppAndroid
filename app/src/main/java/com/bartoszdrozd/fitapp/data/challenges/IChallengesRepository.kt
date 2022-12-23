package com.bartoszdrozd.fitapp.data.challenges

import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import kotlinx.coroutines.flow.Flow

interface IChallengesRepository {
    fun getChallenges() : Flow<List<ChallengeEntry>>
}