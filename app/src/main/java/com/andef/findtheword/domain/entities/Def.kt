package com.andef.findtheword.domain.entities

import com.google.gson.annotations.SerializedName

data class Def(
    @SerializedName("text")
    val word: String,
    @SerializedName("pos")
    val pos: String
)