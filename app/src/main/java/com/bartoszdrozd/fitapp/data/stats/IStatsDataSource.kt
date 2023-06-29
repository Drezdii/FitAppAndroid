package com.bartoszdrozd.fitapp.data.stats

import com.bartoszdrozd.fitapp.model.stats.BodyWeightEntry

interface IStatsDataSource {
    suspend fun saveBodyWeightEntry(entry: BodyWeightEntry): BodyWeightEntry
    suspend fun getLatestBodyWeightEntry(): BodyWeightEntry?
}