package com.example.calbon.retrofit

import com.example.calbon.api.ApiPerguntas
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Importe a interface criada

object RetrofitMongoClient {

    private const val BASE_URL = "https://api-mongo-hi4a.onrender.com/"

    val instance: ApiPerguntas by lazy {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiPerguntas::class.java)
    }

}