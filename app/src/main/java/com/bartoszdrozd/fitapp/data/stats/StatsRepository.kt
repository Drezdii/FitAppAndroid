package com.bartoszdrozd.fitapp.data.stats

import com.bartoszdrozd.fitapp.model.stats.BodyWeightEntry
import javax.inject.Inject

class StatsRepository @Inject constructor(private val remoteDataSource: IStatsDataSource) :
    IStatsRepository {
    override suspend fun saveBodyWeightEntry(entry: BodyWeightEntry): BodyWeightEntry {
        return remoteDataSource.saveBodyWeightEntry(entry)
    }

    override suspend fun getLatestBodyWeightEntry(): BodyWeightEntry? {
        return remoteDataSource.getLatestBodyWeightEntry()
    }
}