package com.andef.findtheword.domain.usecases

import androidx.lifecycle.LiveData
import com.andef.findtheword.data.repository.GameRepositoryImpl


object GetAnagrams {
    fun execute(): LiveData<HashSet<String>> {
        return GameRepositoryImpl.anagramsLiveData
    }
}