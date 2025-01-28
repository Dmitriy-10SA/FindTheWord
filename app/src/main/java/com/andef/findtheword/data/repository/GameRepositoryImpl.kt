package com.andef.findtheword.data.repository

import androidx.lifecycle.MutableLiveData
import com.andef.findtheword.data.api.ApiFactory
import com.andef.findtheword.data.datasource.WordAnagrams
import com.andef.findtheword.domain.entities.WordFromAPI
import com.andef.findtheword.domain.repository.GameRepository
import io.reactivex.Single


object GameRepositoryImpl: GameRepository {
    val anagramsLiveData = MutableLiveData<HashSet<String>>()

    private val apiService = ApiFactory.getInstance()
    private val anagrams = WordAnagrams.anagrams

    override fun checkWord(word: String): Single<WordFromAPI> {
        return apiService.checkWord(word)
    }

    override fun addAnagram(word: String): Boolean {
        if (!anagrams.contains(word)) {
            anagrams.add(word)
            notifyAnagrams()
            return true
        }
        return false
    }

    override fun clearAnagrams() {
        anagrams.clear()
        notifyAnagrams()
    }

    override fun checkAnagramForWord(word: String, anagram: String): Boolean {
        val charAndCnt = HashMap<Char, Int>()
        for (char in word) {
            if (charAndCnt.contains(char)) {
                charAndCnt[char] = charAndCnt.getValue(char) + 1
            } else {
                charAndCnt[char] = 1
            }
        }
        for (char in anagram) {
            if (charAndCnt.contains(char)) {
                val value = charAndCnt.getValue(char) - 1
                if (value < 0) {
                    return false
                } else {
                    charAndCnt[char] = value
                }
            } else {
                return false
            }
        }
        return true
    }

    private fun notifyAnagrams() {
        anagramsLiveData.value = anagrams
    }
}