package com.example.calbon.model

import com.google.gson.annotations.SerializedName

data class ResultadoRecebido(
    @SerializedName("_id")
    val id: String,
    @SerializedName("numero_cracha")
    val numeroCracha: Int,
    @SerializedName("data_resposta")
    val dataResposta: String,
    @SerializedName("nivel_emissao")
    val nivelEmissao: Double, // Ou Float, dependendo da precisão
    @SerializedName("classificacao_emissao")
    val classificacaoEmissao: String,
    @SerializedName("respostas")
    val respostas: List<RespostaEnvio>
)