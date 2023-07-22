package com.bartoszdrozd.fitapp.di

import android.content.Context
import androidx.room.Room
import com.bartoszdrozd.fitapp.AppDatabase
import com.bartoszdrozd.fitapp.data.auth.IAuthService
import com.bartoszdrozd.fitapp.data.auth.IUserRepository
import com.bartoszdrozd.fitapp.data.auth.UserRepository
import com.bartoszdrozd.fitapp.data.challenges.*
import com.bartoszdrozd.fitapp.data.stats.IStatsDataSource
import com.bartoszdrozd.fitapp.data.stats.IStatsRepository
import com.bartoszdrozd.fitapp.data.stats.IStatsService
import com.bartoszdrozd.fitapp.data.stats.StatsRemoteDataSource
import com.bartoszdrozd.fitapp.data.stats.StatsRepository
import com.bartoszdrozd.fitapp.data.workout.*
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providesStatsRepository(@Named("statsRemoteDataSource") remoteDataSource: IStatsDataSource): IStatsRepository =
        StatsRepository(remoteDataSource)

    @Provides
    @Singleton
    @Named("statsRemoteDataSource")
    fun providesStatsLocalDataSource(
        statsService: IStatsService,
        userRepository: IUserRepository
    ): IStatsDataSource = StatsRemoteDataSource(statsService, userRepository)

    @Provides
    @Singleton
    fun providesUserRepository(service: IAuthService, gson: Gson): IUserRepository =
        UserRepository(service, gson)

    @Provides
    @Singleton
    fun providesWorkoutRepository(
        @Named("workoutRemoteDataSource") remoteDataSource: IWorkoutDataSource,
        @Named("workoutLocalDataSource") localDataSource: IWorkoutDataSource,
    ): IWorkoutRepository =
        WorkoutRepository(remoteDataSource, localDataSource)

    @Provides
    @Singleton
    @Named("workoutRemoteDataSource")
    fun providesWorkoutRemoteDataSource(
        workoutService: IWorkoutService,
        userRepository: IUserRepository
    ): IWorkoutDataSource =
        WorkoutRemoteDataSource(workoutService, userRepository)

    @Provides
    @Singleton
    @Named("workoutLocalDataSource")
    fun providesWorkoutLocalDataSource(workoutDao: WorkoutDao): IWorkoutDataSource =
        WorkoutLocalDataSource(workoutDao)

    @Provides
    @Singleton
    fun providesChallengesRemoteDataSource(
        challengesService: IChallengesService,
        userRepository: IUserRepository
    ): IChallengesDataSource =
        ChallengesRemoteDataSource(challengesService, userRepository)

    @Provides
    @Singleton
    fun providesChallengesRepository(
        remoteDataSource: IChallengesDataSource
    ): IChallengesRepository = ChallengesRepository(remoteDataSource)

    @Provides
    @Singleton
    fun providesWorkoutDao(database: AppDatabase): WorkoutDao = database.workoutDao()

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "FitAppDb")
            .fallbackToDestructiveMigration().build()
}