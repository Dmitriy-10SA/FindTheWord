package com.andef.findtheword.domain.usecases

import com.andef.findtheword.data.repository.GameRepositoryImpl

object CheckAnagramForWord {
    fun execute(word: String, anagram: String): Boolean {
        return GameRepositoryImpl.checkAnagramForWord(word, anagram)
    }
}