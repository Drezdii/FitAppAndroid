package com.bartoszdrozd.fitapp.data.stats

import com.bartoszdrozd.fitapp.data.dtos.BodyWeightEntryDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IStatsService {
    @GET("stats/bodyweight/{userId}")
    suspend fun getLatestBodyWeightEntry(@Path("userId") userId: String): Response<BodyWeightEntryDTO>

    @POST("stats/bodyweight")
    suspend fun saveBodyWeightEntry(@Body entry: BodyWeightEntryDTO): Response<BodyWeightEntryDTO>
}