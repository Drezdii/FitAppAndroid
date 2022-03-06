package com.bartoszdrozd.fitapp.di

import com.apollographql.apollo3.ApolloClient
import com.bartoszdrozd.fitapp.data.auth.IAuthService
import com.bartoszdrozd.fitapp.data.auth.IUserRepository
import com.bartoszdrozd.fitapp.data.auth.UserRepository
import com.bartoszdrozd.fitapp.data.workout.IWorkoutDataSource
import com.bartoszdrozd.fitapp.data.workout.IWorkoutRepository
import com.bartoszdrozd.fitapp.data.workout.WorkoutRemoteDataSource
import com.bartoszdrozd.fitapp.data.workout.WorkoutRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun providesWorkoutRepository(@Named("workoutRemoteDataSource") remoteDataSource: IWorkoutDataSource): IWorkoutRepository =
        WorkoutRepository(remoteDataSource)

    @Provides
    @Singleton
    @Named("workoutRemoteDataSource")
    fun providesWorkoutRemoteDataSource(apolloClient: ApolloClient): IWorkoutDataSource =
        WorkoutRemoteDataSource(apolloClient)
}