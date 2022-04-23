package com.bartoszdrozd.fitapp.di

import android.content.Context
import androidx.room.Room
import com.apollographql.apollo3.ApolloClient
import com.bartoszdrozd.fitapp.AppDatabase
import com.bartoszdrozd.fitapp.data.auth.IAuthService
import com.bartoszdrozd.fitapp.data.auth.IUserRepository
import com.bartoszdrozd.fitapp.data.auth.UserRepository
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
    fun providesWorkoutRemoteDataSource(apolloClient: ApolloClient): IWorkoutDataSource =
        WorkoutRemoteDataSource(apolloClient)

    @Provides
    @Singleton
    @Named("workoutLocalDataSource")
    fun providesWorkoutLocalDataSource(workoutDao: WorkoutDao): IWorkoutDataSource =
        WorkoutLocalDataSource(workoutDao)

    @Provides
    @Singleton
    fun providesWorkoutDao(database: AppDatabase): WorkoutDao = database.workoutDao()

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "FitAppDb")
            .fallbackToDestructiveMigration().build()
}