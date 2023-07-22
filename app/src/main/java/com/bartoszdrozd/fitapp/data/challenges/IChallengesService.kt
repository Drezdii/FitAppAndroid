package com.bartoszdrozd.fitapp.data.challenges

import com.bartoszdrozd.fitapp.data.dtos.ChallengeEntryDTO
import com.bartoszdrozd.fitapp.data.dtos.PersonalBestDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IChallengesService {
    @GET("challenges/user/{userId}")
    suspend fun getChallenges(@Path("userId") userId: String): Response<List<ChallengeEntryDTO>>

    @GET("challenges/onerepmaxes/{userId}")
    suspend fun getPersonalBests(@Path("userId") userId: String): Response<List<PersonalBestDTO>>

    @GET("challenges/{userId}/top3")
    suspend fun getClosestChallenges(@Path("userId") userId: String): Response<List<ChallengeEntryDTO>>
}