package com.example.calbon.api

import com.example.calbon.model.Noticia
import com.google.android.gms.common.api.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiNoticia {
    @GET("noticias/listar")
    suspend fun listarNoticias(
        @Query("limite") limite: Int = 10 // limite de notícias
    ): List<Noticia>
}
