package com.andef.findtheword.domain.repository

import androidx.lifecycle.LiveData
import com.andef.findtheword.domain.entities.WordFromAPI
import io.reactivex.Single

interface GameRepository {
    fun checkWord(word: String): Single<WordFromAPI>
    fun addAnagram(word: String): Boolean
    fun clearAnagrams()
    fun checkAnagramForWord(word: String, anagram: String): Boolean
    fun resumeAnagrams(anagrams: HashSet<String>)
    val anagramsLiveData: LiveData<HashSet<String>>
}