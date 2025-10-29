package com.example.calbon.api

data class AtualizacaoPerfil(
    val nome: String? = null,
    val sobrenome: String? = null,
    val email: String? = null,
    val senha: String? = null,
    val fotoUrl: String? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nome" to nome,
            "sobrenome" to sobrenome,
            "email" to email,
            "senha" to senha,
            "fotoUrl" to fotoUrl
        )
    }
}
