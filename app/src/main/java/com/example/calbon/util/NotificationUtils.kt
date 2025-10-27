package com.example.calbon.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.calbon.R
import com.example.calbon.model.Notificacao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object NotificationUtils {

    private const val CHANNEL_ID = "CALBON_CHANNEL"
    private const val CHANNEL_NAME = "Notificações do Calbon"
    private const val CHANNEL_DESC = "Notificações importantes do app"
    private const val PREFS_NAME = "NOTIFICATIONS_PREFS"

    /** Cria canal de notificação (Android 8+) */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                setShowBadge(true)
            }
            val manager = context.getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /** Envia notificação genérica e salva no histórico do usuário */
    fun sendNotification(
        context: Context,
        numeroCracha: Int,
        title: String,
        message: String,
        intent: Intent? = null
    ) {
        // Canal
        createNotificationChannel(context)

        // Permissão Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (context is android.app.Activity) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            } else {
                Toast.makeText(context, "Permissão de notificação necessária", Toast.LENGTH_SHORT)
                    .show()
            }
            return
        }

        // PendingIntent
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context, 0, it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Criar notificação
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .apply { if (pendingIntent != null) setContentIntent(pendingIntent) }

        NotificationManagerCompat.from(context).notify(numeroCracha, builder.build())

        // Salvar localmente
        salvarNotificacaoLocal(context, numeroCracha, Notificacao(title, message, System.currentTimeMillis()))
    }

    /** Notificação de login */
    fun showWelcomeNotification(context: Context, numeroCracha: Int, nomeUsuario: String) {
        sendNotification(context, numeroCracha, "Login realizado", "Bem-vindo(a), $nomeUsuario!")
    }

    /** Notificação de atualização de perfil */
    fun showProfileUpdatedNotification(context: Context, numeroCracha: Int, nomeUsuario: String) {
        sendNotification(context, numeroCracha, "Informações atualizadas", "Olá $nomeUsuario, suas informações foram atualizadas com sucesso")
    }

    /** Salva notificação local por usuário */
    private fun salvarNotificacaoLocal(context: Context, numeroCracha: Int, notificacao: Notificacao) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = "$numeroCracha" // cada usuário tem uma chave
        val json = prefs.getString(key, null)
        val lista = if (json != null) {
            Gson().fromJson<MutableList<Notificacao>>(json, object : TypeToken<MutableList<Notificacao>>() {}.type)
        } else {
            mutableListOf()
        }
        lista.add(0, notificacao) // adiciona no início
        prefs.edit().putString(key, Gson().toJson(lista)).apply()
    }

    /** Retorna histórico de notificações de um usuário */
    fun getNotificacoes(context: Context, numeroCracha: Int): List<Notificacao> {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = "$numeroCracha"
        val json = prefs.getString(key, null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<List<Notificacao>>() {}.type)
        } else {
            emptyList()
        }
    }
}
