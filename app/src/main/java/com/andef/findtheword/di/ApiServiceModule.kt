package com.andef.findtheword.di

import com.andef.findtheword.data.api.ApiFactory
import com.andef.findtheword.data.api.ApiService
import dagger.Module
import dagger.Provides

@Module
class ApiServiceModule {
    @Provides
    fun provideApiService(): ApiService {
        return ApiFactory.getInstance()
    }
}