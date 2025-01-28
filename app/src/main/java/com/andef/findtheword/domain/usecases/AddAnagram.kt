package com.andef.findtheword.domain.usecases

import com.andef.findtheword.data.repository.GameRepositoryImpl


object AddAnagram {
    fun execute(word: String): Boolean {
        return GameRepositoryImpl.addAnagram(word)
    }
}