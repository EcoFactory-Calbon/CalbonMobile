package com.example.calbon.model

import com.google.gson.annotations.SerializedName

data class ResultadoRecebido(
    @SerializedName("_id")
    val id: String,
    @SerializedName("numeroCracha")
    val numeroCracha: String,
    @SerializedName("dataResposta")
    val dataResposta: String,
    @SerializedName("nivelEmissao")
    val nivelEmissao: Double,
    @SerializedName("classificacaoEmissao")
    val classificacaoEmissao: String,
    @SerializedName("respostas")
    val respostas: List<RespostaItemEnvio>
)