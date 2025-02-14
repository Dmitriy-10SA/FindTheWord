package com.andef.findtheword.domain.usecases

import com.andef.findtheword.domain.repository.GameRepository
import javax.inject.Inject


class AddAnagramUseCase @Inject constructor(
    private val repository: GameRepository
) {
    fun execute(word: String): Boolean {
        return repository.addAnagram(word)
    }
}