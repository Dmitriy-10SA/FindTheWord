package com.andef.findtheword.di

import com.andef.findtheword.presentation.ui.GameActivity
import com.andef.findtheword.presentation.ui.MainActivity
import dagger.Component

@ApplicationScope
@Component(modules = [GameRepositoryModule::class, ApiServiceModule::class, ViewModelModule::class])
interface FindTheWordComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: GameActivity)
}