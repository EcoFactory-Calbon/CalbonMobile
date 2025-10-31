package com.example.calbon.util

import android.content.Context

object SessionManager {

    private const val PREFS_NAME = "APP_PREFS"
    private const val KEY_TOKEN = "TOKEN"
    private const val KEY_CRACHA = "NUMERO_CRACHA"

    fun saveSession(context: Context, token: String, numeroCracha: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_CRACHA, numeroCracha)
            apply()
        }
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getNumeroCracha(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_CRACHA, -1)
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
