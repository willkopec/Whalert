package com.willkopec.whalert.model.newsAPI

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)