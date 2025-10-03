package com.example.calbon.api

import com.example.calbon.model.LinkItem
import retrofit2.http.GET

interface ApiService {
    // Endpoint para buscar os links
    @GET("links") // <-- ajuste aqui para o endpoint real da sua API
    suspend fun getLinks(): List<LinkItem>
}