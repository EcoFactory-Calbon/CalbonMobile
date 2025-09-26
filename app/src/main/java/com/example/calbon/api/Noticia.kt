package com.example.calbon.api

import com.example.calbon.model.LinksResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface Noticia {
    @GET
    suspend fun getLinks(@Url endpoint: String): LinksResponse
}