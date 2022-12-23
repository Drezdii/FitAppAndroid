package com.bartoszdrozd.fitapp.data.challenges

import com.bartoszdrozd.fitapp.data.dtos.ChallengeEntryDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IChallengesService {
    @GET("challenges/user/{userId}")
    suspend fun getChallenges(@Path("userId") userId: String): Response<List<ChallengeEntryDTO>>
}