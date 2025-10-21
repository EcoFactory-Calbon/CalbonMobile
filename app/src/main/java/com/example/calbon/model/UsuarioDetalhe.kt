package com.example.calbon.api

data class UsuarioDetalhe(
    val numeroCracha: Int,
    val nome: String,
    val sobrenome: String,
    val email: String,
    val cargo: String?,
    val setor: String?
)
