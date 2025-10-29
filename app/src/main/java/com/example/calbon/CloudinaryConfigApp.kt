package com.example.calbon

import android.app.Application
import android.util.Log
import com.cloudinary.android.MediaManager

class CloudinaryConfigApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Defina suas credenciais
        val config = mapOf(
            "cloud_name" to "dzdamwbma",
            "api_key" to "888539645355774",
            "api_secret" to "n0RxqK6bmOEmknm8gJ1LPm62-gY"
        )

        // 2. Inicialize o Cloudinary usando try-catch para evitar inicializações duplicadas
        try {
            // A inicialização é feita aqui. Se já foi feita, um erro será lançado e capturado.
            MediaManager.init(this, config)
            Log.d("Cloudinary", "Cloudinary inicializado com sucesso.")
        } catch (e: IllegalStateException) {
            // Este erro é esperado se a biblioteca já estiver inicializada, então ignoramos.
            Log.w("Cloudinary", "Cloudinary já estava inicializado, ignorando.", e)
        } catch (e: Exception) {
            // Outros erros graves na inicialização
            Log.e("Cloudinary", "Erro grave ao inicializar Cloudinary", e)
        }
    }
}