package com.andef.findtheword.domain.usecases

import androidx.lifecycle.LiveData
import com.andef.findtheword.data.repository.GameRepositoryImpl
import com.andef.findtheword.domain.repository.GameRepository
import javax.inject.Inject


class GetAnagramsUseCase @Inject constructor(
    private val repository: GameRepository
) {
    fun execute(): LiveData<HashSet<String>> {
        return repository.anagramsLiveData
    }
}