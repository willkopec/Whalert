package com.willkopec.whalert.api

import com.willkopec.whalert.model.newsAPI.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("v2/everything")
    suspend fun getCryptoNews(
        @Query("q")
        searchQuery: String = "btc crypto blockchain",
        @Query("page")
        pageNumber :  Int = 1,
        @Query("apiKey")
        apiKey: String = "cb31890b2d674fbebbe05114ab2ea4d5"
    ): NewsResponse
}