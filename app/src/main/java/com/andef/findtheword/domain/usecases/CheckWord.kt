package com.andef.findtheword.domain.usecases

import com.andef.findtheword.data.repository.GameRepositoryImpl
import com.andef.findtheword.domain.entities.WordFromAPI
import io.reactivex.Single

object CheckWord {
    fun execute(word: String): Single<WordFromAPI> {
        return GameRepositoryImpl.checkWord(word)
    }
}