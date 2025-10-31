package com.example.calbon.model

import com.google.gson.annotations.SerializedName

data class RespostaItemEnvio(
    @SerializedName("idPergunta")
    val idPergunta: Int,
    @SerializedName("resposta")
    val resposta: Int
)