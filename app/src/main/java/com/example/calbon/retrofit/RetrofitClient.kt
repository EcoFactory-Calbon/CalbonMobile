package com.example.calbon.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL_API = "https://api-sql-pdlt.onrender.com/"

    // Retrofit com autenticação
    private fun getRetrofitComAuth(context: Context): Retrofit {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // adiciona token JWT
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit sem autenticação
    private fun getRetrofitSemAuth(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getApiUsuario(context: Context): UsuarioApi = getRetrofitComAuth(context).create(UsuarioApi::class.java)

    fun getApiUsuarioSemAuth(): UsuarioApi = getRetrofitSemAuth().create(UsuarioApi::class.java)

}
