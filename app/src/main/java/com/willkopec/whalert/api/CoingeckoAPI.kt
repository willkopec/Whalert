package com.willkopec.whalert.api

import com.willkopec.whalert.model.CryptoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CoingeckoAPI {

    @GET("api/v3/coins/markets")
    suspend fun getBreakingNews(
        @Query("vs_currency")
        countryCode: String = "usd"
    ): CryptoResponse


}