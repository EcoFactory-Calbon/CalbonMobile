package com.example.calbon.model

// Import necessário para o Retrofit/Gson para que o JSON seja deserializado
import com.google.gson.annotations.SerializedName

data class Pergunta(
    @SerializedName("id")
    val id: Int,

    @SerializedName("pergunta")
    val pergunta: String,

    @SerializedName("categoria")
    val categoria: String
)