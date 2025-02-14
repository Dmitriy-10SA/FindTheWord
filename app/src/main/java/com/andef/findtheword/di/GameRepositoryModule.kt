package com.andef.findtheword.di

import com.andef.findtheword.data.repository.GameRepositoryImpl
import com.andef.findtheword.domain.repository.GameRepository
import dagger.Binds
import dagger.Module

@Module
interface GameRepositoryModule {
    @Binds
    fun bindGameRepository(impl: GameRepositoryImpl): GameRepository
}