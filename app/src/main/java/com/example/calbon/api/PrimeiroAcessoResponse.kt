package com.example.calbon.api

data class PrimeiroAcessoResponse(
    val numeroCracha: Int,
    val nome: String,
    val sobrenome: String,
    val email: String,
    val is_gestor: Boolean,
    val id_Cargo: Int,
    val id_Localizacao: Int
)
