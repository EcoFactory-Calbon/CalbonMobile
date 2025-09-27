package com.example.calbon.api

import com.example.calbon.model.LinkItem
import com.example.calbon.model.LinksResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface Noticia {
    @GET("joycenick/ApiTestNoticias/main/links.json")
    suspend fun getLinks(): List<LinkItem>
}