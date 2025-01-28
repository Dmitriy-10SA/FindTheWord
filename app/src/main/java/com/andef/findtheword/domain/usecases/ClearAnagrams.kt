package com.andef.findtheword.domain.usecases

import com.andef.findtheword.data.repository.GameRepositoryImpl


object ClearAnagrams {
    fun execute() {
        GameRepositoryImpl.clearAnagrams()
    }
}