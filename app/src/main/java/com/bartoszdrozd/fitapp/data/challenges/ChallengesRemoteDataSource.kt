package com.bartoszdrozd.fitapp.data.challenges

import com.bartoszdrozd.fitapp.data.auth.IUserRepository
import com.bartoszdrozd.fitapp.data.dtos.ChallengeEntryDTO
import com.bartoszdrozd.fitapp.data.dtos.PersonalBestDTO
import com.bartoszdrozd.fitapp.model.challenges.ChallengeEntry
import com.bartoszdrozd.fitapp.model.stats.PersonalBest
import com.bartoszdrozd.fitapp.utils.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChallengesRemoteDataSource @Inject constructor(
    private val challengesService: IChallengesService,
    private val userRepository: IUserRepository
) : IChallengesDataSource {
    override fun getChallenges(): Flow<List<ChallengeEntry>> = flow {
        // TODO: Do something about userId possibly being null
        val res = challengesService.getChallenges(userRepository.getUserId() ?: "")

        if (res.isSuccessful) {
            emit(res.body()!!.map(ChallengeEntryDTO::toModel))
        } else {
            // Emit error loading challenges from the server
        }
    }

    override fun getPersonalBests(): Flow<List<PersonalBest>> = flow {
        val res = challengesService.getPersonalBests(userRepository.getUserId() ?: "")

        if (res.isSuccessful) {
            emit(res.body()!!.map(PersonalBestDTO::toModel))
        } else {
            // Emit error loading personal bests from the server
        }
    }

    override fun getClosestChallenges(): Flow<List<ChallengeEntry>> = flow {
        val res = challengesService.getClosestChallenges(userRepository.getUserId() ?: "")

        if (res.isSuccessful) {
            emit(res.body()!!.map(ChallengeEntryDTO::toModel))
        } else {
            // Emit error loading personal bests from the server
        }
    }
}