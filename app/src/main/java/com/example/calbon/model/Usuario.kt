package com.example.calbon.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    val numeroCracha: Int,
    val nome: String,
    val sobrenome: String,
    val email: String,
    val is_gestor: Boolean,
    val id_Cargo: Int,
    val id_Localizacao: Int
) : Parcelable
