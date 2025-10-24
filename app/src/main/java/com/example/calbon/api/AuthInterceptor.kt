package com.example.calbon.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val url = chain.request().url.toString()

        if (!url.contains("/auth/")) { // adiciona token só em endpoints protegidos
            val prefs = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
            val token = prefs.getString("TOKEN", null)
            Log.d("AUTH_INTERCEPTOR", "Token usado: $token")
            token?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}
