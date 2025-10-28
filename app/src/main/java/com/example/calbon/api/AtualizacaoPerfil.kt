package com.example.calbon.api

data class AtualizacaoPerfil(
    val nome: String? = null,
    val sobrenome: String? = null,
    val email: String? = null,
    val senha: String? = null,
    val fotoUrl: String? = null
)