package com.example.calbon.api

import com.example.calbon.model.ResultadoRecebido
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiFormulario {

    @GET("formulario/buscarCracha/{cracha}")
    suspend fun buscarFormulariosPorCracha(
        @Path("cracha") cracha: String
    ): Response<List<ResultadoRecebido>>
}
