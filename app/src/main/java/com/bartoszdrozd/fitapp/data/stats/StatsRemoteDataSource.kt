package com.bartoszdrozd.fitapp.data.stats

import android.util.Log
import com.bartoszdrozd.fitapp.data.auth.IUserRepository
import com.bartoszdrozd.fitapp.model.stats.BodyWeightEntry
import com.bartoszdrozd.fitapp.utils.toDTO
import com.bartoszdrozd.fitapp.utils.toModel
import javax.inject.Inject

class StatsRemoteDataSource @Inject constructor(
    private val service: IStatsService,
    private val userRepository: IUserRepository
) : IStatsDataSource {
    override suspend fun saveBodyWeightEntry(entry: BodyWeightEntry): BodyWeightEntry {
        val res = service.saveBodyWeightEntry(entry.toDTO())

        if (res.isSuccessful) {
            return res.body()!!.toModel()
        } else {
            Log.e("TEST", res.errorBody()?.string().toString())
            throw Exception(res.errorBody()?.string())
        }
    }

    override suspend fun getLatestBodyWeightEntry(): BodyWeightEntry {
        val res = service.getLatestBodyWeightEntry(userRepository.getUserId() ?: "")

        if (res.isSuccessful) {
            return res.body()!!.toModel()
        } else {
            Log.e("TEST", res.errorBody()?.string().toString())
            throw Exception(res.errorBody()?.string())
        }
    }
}