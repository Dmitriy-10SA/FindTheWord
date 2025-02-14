package com.andef.findtheword.data.dto

import com.google.gson.annotations.SerializedName

data class DefDto(
    @SerializedName("text")
    val word: String,
    @SerializedName("pos")
    val pos: String
)