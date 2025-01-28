package com.andef.findtheword.domain.usecases

import com.andef.findtheword.data.repository.GameRepositoryImpl

object ResumeAnagrams {
    fun execute(anagrams: HashSet<String>) {
        GameRepositoryImpl.resumeAnagrams(anagrams)
    }
}