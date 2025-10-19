package com.example.calbon.api

import retrofit2.Response
import retrofit2.http.*

interface UsuarioApi {

        // Retorna todos os usuários (teste)
        @GET("joycenick/ApiTestNoticias/main/usuario.json")
        suspend fun getUsers(): List<Usuario>

        // Buscar usuário por crachá
        @GET("funcionario/buscarCracha/{cracha}")
        suspend fun buscarPorCracha(
                @Path("cracha") cracha: Int
        ): Response<Usuario>

        // Buscar usuários por empresa
        @GET("funcionario/buscarEmpresa/{id}")
        suspend fun buscarPorEmpresa(
                @Path("id") idEmpresa: Int
        ): Response<List<UsuarioDetalhe>>

        // Atualizar parcialmente um usuário
        @PATCH("funcionario/atualizar/{id}")
        suspend fun atualizarUsuario(
                @Header("Authorization") token: String, // token JWT
                @Path("id") id: Long,
                @Body camposAtualizados: Map<String, Any>
        ): Response<Usuario>
}
