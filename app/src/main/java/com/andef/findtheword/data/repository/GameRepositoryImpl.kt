package com.andef.findtheword.data.repository

import androidx.lifecycle.MutableLiveData
import com.andef.findtheword.data.api.ApiService
import com.andef.findtheword.data.datasource.WordAnagrams
import com.andef.findtheword.data.mapper.DtoMapper
import com.andef.findtheword.di.ApplicationScope
import com.andef.findtheword.domain.entities.WordFromAPI
import com.andef.findtheword.domain.repository.GameRepository
import io.reactivex.Single
import javax.inject.Inject

@ApplicationScope
class GameRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : GameRepository {
    override val anagramsLiveData = MutableLiveData<HashSet<String>>()

    private var anagrams = WordAnagrams.anagrams

    override fun checkWord(word: String): Single<WordFromAPI> {
        return apiService.checkWord(word).map {
            DtoMapper.mapWordFromApiDtoToWordFromApi(it)
        }
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

    override fun resumeAnagrams(anagrams: HashSet<String>) {
        WordAnagrams.anagrams = anagrams
        this.anagrams = WordAnagrams.anagrams
        notifyAnagrams()
    }

    private fun notifyAnagrams() {
        anagramsLiveData.value = anagrams
    }
}