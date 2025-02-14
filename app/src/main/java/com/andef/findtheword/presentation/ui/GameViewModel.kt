package com.andef.findtheword.presentation.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andef.findtheword.domain.usecases.AddAnagramUseCase
import com.andef.findtheword.domain.usecases.CheckAnagramForWordUseCase
import com.andef.findtheword.domain.usecases.CheckWordUseCase
import com.andef.findtheword.domain.usecases.ClearAnagramsUseCase
import com.andef.findtheword.domain.usecases.GetAnagramsUseCase
import com.andef.findtheword.domain.usecases.ResumeAnagramsUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


class GameViewModel @Inject constructor(
    private val addAnagramUseCase: AddAnagramUseCase,
    private val resumeAnagramsUseCase: ResumeAnagramsUseCase,
    private val checkAnagramForWordUseCase: CheckAnagramForWordUseCase,
    private val checkWordUseCase: CheckWordUseCase,
    private val getAnagramsUseCase: GetAnagramsUseCase,
    private val clearAnagramsUseCase: ClearAnagramsUseCase
): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val isWord = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val isItWas = MutableLiveData<Boolean>()
    val checkInternet = MutableLiveData<Boolean>()

    fun addAnagram(word: String) {
        if (!addAnagramUseCase.execute(word)) {
            isItWas.value = true
        }
    }

    fun resumeAnagrams(anagrams: HashSet<String>) {
        resumeAnagramsUseCase.execute(anagrams)
    }

    fun checkWord(word: String) {
        val disposable = checkWordUseCase.execute(word)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.value = true }
            .doAfterTerminate { isLoading.value = false }
            .subscribe(
                { wordFromApi ->
                    isWord.value = (
                                    wordFromApi.listWithWord.isNotEmpty() &&
                                    wordFromApi.listWithWord[0].word == word &&
                                    wordFromApi.listWithWord[0].pos == "noun"
                                    )
                },
                { e ->
                    Log.e(TAG, e.toString())
                    checkInternet.value = true
                }
            )
        compositeDisposable.add(disposable)
    }

    fun checkAnagramForWord(word: String, anagram: String): Boolean {
        return checkAnagramForWordUseCase.execute(word, anagram)
    }

    fun getAnagrams(): LiveData<HashSet<String>> {
        return getAnagramsUseCase.execute()
    }

    fun clearAnagrams() {
        clearAnagramsUseCase.execute()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        private const val TAG = "GameViewModel"
    }
}