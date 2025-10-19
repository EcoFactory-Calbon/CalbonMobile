package com.example.calbon.api


data class LoginRequest(
    val numeroCracha: Int,
    val senha: String
)

data class LoginResponse(
    val token: String,
    val nome: String,
    val numeroCracha: Int
)