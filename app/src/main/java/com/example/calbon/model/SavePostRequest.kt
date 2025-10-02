package com.example.calbon.model

data class SavePostRequest(
    val userId: String,
    val postId: String,
    val saved: Boolean
)
