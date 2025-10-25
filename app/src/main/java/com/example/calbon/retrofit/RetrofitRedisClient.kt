package com.example.calbon.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit // Import necessário

object RetrofitRedisClient {

    private const val BASE_URL_REDIS = "https://api-redis-ok9n.onrender.com/"

    private fun getRetrofitRedis(): Retrofit {

        // 1. Cria um cliente OkHttp com timeouts estendidos
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS) // Tempo para estabelecer a conexão
            .readTimeout(120, TimeUnit.SECONDS)    // Tempo para receber a resposta
            .writeTimeout(120, TimeUnit.SECONDS)   // Tempo para enviar a requisição (menos crítico aqui)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL_REDIS)
            // 2. Anexa o cliente OkHttp ao Retrofit
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: com.example.calbon.api.ApiNoticia by lazy {
        getRetrofitRedis().create(com.example.calbon.api.ApiNoticia::class.java)
    }
}