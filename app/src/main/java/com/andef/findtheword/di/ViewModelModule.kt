package com.andef.findtheword.di

import android.view.View
import androidx.lifecycle.ViewModel
import com.andef.findtheword.presentation.ui.GameViewModel
import com.andef.findtheword.presentation.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

@Module
interface ViewModelModule {
    @Binds
    @ViewModelKey(MainViewModel::class)
    @IntoMap
    fun bindMainViewModel(impl: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameViewModel::class)
    fun bindGameViewModel(impl: GameViewModel): ViewModel
}