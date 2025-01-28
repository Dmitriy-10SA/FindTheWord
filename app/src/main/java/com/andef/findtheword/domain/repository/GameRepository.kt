package com.andef.findtheword.domain.repository

import com.andef.findtheword.domain.entities.WordFromAPI
import io.reactivex.Single

interface GameRepository {
    fun checkWord(word: String): Single<WordFromAPI>
    fun addAnagram(word: String): Boolean
    fun clearAnagrams()
}