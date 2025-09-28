package com.example.calbon.model

import com.google.gson.annotations.SerializedName

data class LinkItem(
    val id: Int,
    @SerializedName("titulo") val title: String,
    val url: String,
    @SerializedName("imagem") val img: String
)


data class LinksResponse(
    val links: List<LinkItem>

)
