package com.andef.findtheword.presentation.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andef.findtheword.domain.usecases.AddAnagram
import com.andef.findtheword.domain.usecases.CheckAnagramForWord
import com.andef.findtheword.domain.usecases.CheckWord
import com.andef.findtheword.domain.usecases.ClearAnagrams
import com.andef.findtheword.domain.usecases.GetAnagrams
import com.andef.findtheword.domain.usecases.ResumeAnagrams
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers


class GameViewModel(application: Application): AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()

    val isWord = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val isItWas = MutableLiveData<Boolean>()
    val checkInternet = MutableLiveData<Boolean>()

    fun addAnagram(word: String) {
        if (!AddAnagram.execute(word)) {
            isItWas.value = true
        }
    }

    fun resumeAnagrams(anagrams: HashSet<String>) {
        ResumeAnagrams.execute(anagrams)
    }

    fun checkWord(word: String) {
        val disposable = CheckWord.execute(word)
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
        return CheckAnagramForWord.execute(word, anagram)
    }

    fun getAnagrams(): LiveData<HashSet<String>> {
        return GetAnagrams.execute()
    }

    fun clearAnagrams() {
        ClearAnagrams.execute()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        private const val TAG = "GameViewModel"
    }
}