package com.andef.findtheword.domain.entities

import com.google.gson.annotations.SerializedName

data class WordFromAPI(
    @SerializedName("def")
    val listWithWord: List<Def>
)