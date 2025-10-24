package com.example.calbon.api

import DefinirSenhaRequest
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApi {

        // Buscar usuário pelo número do crachá
        @GET("funcionario/buscarCracha/{cracha}")
        suspend fun buscarPorCracha(@Path("cracha") cracha: Int): Response<List<Usuario>>

        // Atualizar parcialmente o perfil do próprio usuário autenticado

                @PATCH("funcionario/AtualizarPerfil")
                suspend fun atualizarPerfil(
                        @Body campos: Map<String, @JvmSuppressWildcards Any>
                ): Response<Void>


        // Atualizar qualquer usuário (requer token JWT)
        @PATCH("funcionario/atualizar/{id}")
        suspend fun atualizarUsuario(
                @Header("Authorization") token: String,
                @Path("id") id: Long,
                @Body camposAtualizados: Map<String, Any>
        ): Response<Usuario>

        // Login
        @POST("auth/funcionario/login")
        suspend fun loginFuncionario(@Body request: LoginRequest): Response<LoginResponse>

        // Primeiro acesso
        @POST("funcionario/primeiroAcesso")
        suspend fun primeiroAcesso(@Body request: PrimeiroAcessoRequest): Response<PrimeiroAcessoResponse>

        @PUT("funcionario/primeiroAcesso/definirSenha/{numeroCracha}")
        suspend fun definirSenha(
                @Path("numeroCracha") numeroCracha: Int,
                @Body request: DefinirSenhaRequest
        ): Response<Void>
}
