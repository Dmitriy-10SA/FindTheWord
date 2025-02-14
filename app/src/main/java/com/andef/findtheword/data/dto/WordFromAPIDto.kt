package com.andef.findtheword.data.dto

import com.google.gson.annotations.SerializedName

data class WordFromAPIDto(
    @SerializedName("def")
    val listWithWord: List<DefDto>
)