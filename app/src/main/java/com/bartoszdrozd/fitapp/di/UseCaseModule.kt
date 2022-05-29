package com.bartoszdrozd.fitapp.di

import com.bartoszdrozd.fitapp.domain.programs.Program531BBB4DaysCreator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provides531BBB4DaysCreator(): Program531BBB4DaysCreator {
        return Program531BBB4DaysCreator()
    }
}