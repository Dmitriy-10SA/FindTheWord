package com.andef.findtheword.domain.usecases

import com.andef.findtheword.domain.repository.GameRepository
import javax.inject.Inject

class ResumeAnagramsUseCase @Inject constructor(
    private val repository: GameRepository
) {
    fun execute(anagrams: HashSet<String>) {
        repository.resumeAnagrams(anagrams)
    }
}