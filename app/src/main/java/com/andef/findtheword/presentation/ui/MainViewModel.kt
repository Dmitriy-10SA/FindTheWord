package com.andef.findtheword.presentation.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andef.findtheword.domain.usecases.CheckWord
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers


class MainViewModel: ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val isWord = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val checkInternet = MutableLiveData<Boolean>()

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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}