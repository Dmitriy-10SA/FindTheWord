package com.andef.findtheword.presentation.app

import android.app.Application
import com.andef.findtheword.di.DaggerFindTheWordComponent

class FindTheWordApplication: Application() {
    val component by lazy {
        DaggerFindTheWordComponent.create()
    }
}