package com.andef.findtheword.presentation.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andef.findtheword.domain.usecases.CheckWord
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {
    private val randomWords = listOf(
        "Газонокосилка", "Демонстрация", "Гуманитарий", "Полезность", "Исследование",
        "Домовладелец", "Хрупкость", "Динозавр", "Компонент", "Скороговорка",
        "Парфюмерия", "Тусовка", "Дюймовочка", "Художник", "Островок",
        "Неготовность", "Наборщик", "Штрудель", "Учительница", "Разговор",
        "Качество", "Вежливость", "Сухофрукты", "Минутка", "Бездомность", "Множитель",
        "Почитатель", "Автобаза", "Притворство", "Подушечка", "Банкомат",
        "Бескультурье", "Загривок", "Транспорт", "Благоухание", "Господин", "Мыслитель",
        "Брудершафт", "Плательщик", "Буратино", "Хронометр", "Стремянка",
        "Естествознание", "Диаграмма", "Размягчение", "Коллектор", "Тельняшка", "Калькулятор"
    )
    private val compositeDisposable = CompositeDisposable()


    val isWord = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val checkInternet = MutableLiveData<Boolean>()

    fun getRandomWord(): String = randomWords.random()

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