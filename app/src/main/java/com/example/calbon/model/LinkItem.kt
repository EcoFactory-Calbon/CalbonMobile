package com.example.calbon.model

data class LinkItem(
    val id: Int,
    val title: String,
    val url: String,
)

data class LinksResponse(
    val links: List<LinkItem>

)
