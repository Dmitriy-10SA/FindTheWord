package com.andef.findtheword.data.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object ApiFactory {
    private const val BASE_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/"

    private var instance: ApiService? = null
    fun getInstance(): ApiService {
        if (instance == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            instance = retrofit.create(ApiService::class.java)
        }
        return instance!!
    }
}