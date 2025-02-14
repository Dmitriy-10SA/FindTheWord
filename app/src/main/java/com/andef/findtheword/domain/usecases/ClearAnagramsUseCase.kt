package com.andef.findtheword.domain.usecases

import com.andef.findtheword.domain.repository.GameRepository
import javax.inject.Inject


class ClearAnagramsUseCase @Inject constructor(
    private val repository: GameRepository
) {
    fun execute() {
        repository.clearAnagrams()
    }
}