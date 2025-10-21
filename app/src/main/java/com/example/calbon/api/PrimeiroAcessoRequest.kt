package com.example.calbon.api

data class PrimeiroAcessoRequest(
    val email: String,
    val numeroCracha: Int,
    val codigoEmpresa: Int
)
