package com.example.calbon.api

import com.example.calbon.model.Pergunta
import com.example.calbon.model.RespostaEnvio
import com.example.calbon.model.ResultadoRecebido
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiPerguntas {

    @GET("pergunta/listar")
    suspend fun listarPerguntas(): Response<List<Pergunta>>

    @POST("formulario/inserir")
    suspend fun enviarRespostas(@Body resultado: RespostaEnvio): Response<ResultadoRecebido>
}