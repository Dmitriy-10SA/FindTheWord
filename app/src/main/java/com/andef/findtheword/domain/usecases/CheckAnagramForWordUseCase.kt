package com.andef.findtheword.domain.usecases

import com.andef.findtheword.domain.repository.GameRepository
import javax.inject.Inject

class CheckAnagramForWordUseCase @Inject constructor(
    private val repository: GameRepository
) {
    fun execute(word: String, anagram: String): Boolean {
        return repository.checkAnagramForWord(word, anagram)
    }
}