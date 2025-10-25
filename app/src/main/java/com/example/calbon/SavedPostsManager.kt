package com.example.calbon.util

import android.content.Context
import android.content.SharedPreferences

object SavedPostsManager {

    private const val PREFS_NAME = "saved_posts_prefs"
    private const val SAVED_POSTS_KEY = "saved_posts"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isPostSaved(context: Context, postId: String): Boolean {
        val savedSet = getPrefs(context).getStringSet(SAVED_POSTS_KEY, emptySet()) ?: emptySet()
        return savedSet.contains(postId)
    }

    fun savePostStatus(context: Context, postId: String, isSaved: Boolean) {
        val prefs = getPrefs(context)
        val savedSet = prefs.getStringSet(SAVED_POSTS_KEY, mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        if (isSaved) {
            savedSet.add(postId)
        } else {
            savedSet.remove(postId)
        }

        prefs.edit().putStringSet(SAVED_POSTS_KEY, savedSet).apply()
    }

    fun getSavedPosts(context: Context): Set<String> {
        return getPrefs(context).getStringSet(SAVED_POSTS_KEY, emptySet()) ?: emptySet()
    }
}
