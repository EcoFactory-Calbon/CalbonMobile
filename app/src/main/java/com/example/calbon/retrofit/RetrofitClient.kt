package com.example.calbon.api

import UsuarioApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL_API = "https://api-sql-pdlt.onrender.com/"

    private fun getRetrofitApi(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiUsuario: UsuarioApi by lazy {
        getRetrofitApi().create(UsuarioApi::class.java)
    }
}
