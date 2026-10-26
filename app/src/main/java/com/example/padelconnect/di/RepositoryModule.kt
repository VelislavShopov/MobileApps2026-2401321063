package com.example.padelconnect.di

import com.example.padelconnect.data.repository.MatchCreator
import com.example.padelconnect.data.repository.MatchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMatchCreator(
        repository: MatchRepository
    ): MatchCreator
}