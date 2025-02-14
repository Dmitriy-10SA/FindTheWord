package com.andef.findtheword.domain.usecases

import com.andef.findtheword.domain.entities.WordFromAPI
import com.andef.findtheword.domain.repository.GameRepository
import io.reactivex.Single
import javax.inject.Inject

class CheckWordUseCase @Inject constructor(
    private val repository: GameRepository
) {
    fun execute(word: String): Single<WordFromAPI> {
        return repository.checkWord(word)
    }
}