package com.example.calbon.model

import com.google.gson.annotations.SerializedName

data class RespostaEnvio(
    @SerializedName("numeroCracha")
    val numeroCracha: String,
    @SerializedName("respostas")
    val respostas: List<RespostaItemEnvio>
)