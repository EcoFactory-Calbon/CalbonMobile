package com.example.calbon.util

import android.content.Context
import android.content.SharedPreferences

object SavedPostsManager {

    private const val PREFS_NAME = "saved_posts"

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isPostSaved(context: Context, postId: String): Boolean {
        return getPrefs(context).getBoolean(postId, false)
    }

    fun savePostStatus(context: Context, postId: String, saved: Boolean) {
        getPrefs(context).edit().putBoolean(postId, saved).apply()
    }
}
