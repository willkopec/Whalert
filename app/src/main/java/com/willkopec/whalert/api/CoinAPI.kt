package com.willkopec.whalert.api

import com.willkopec.whalert.model.coinAPI.CoinAPIResult
import com.willkopec.whalert.util.DateUtil.getDateBeforeDaysWithTime
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinAPI {

    @GET("v1/ohlcv/{symbol}/{apiKey}/history")
    suspend fun getSymbolData(
        @Path("symbol") symbol: String,
        @Path("apiKey") apiKey: String = "apikey-A5B83F88-B4B4-4D3E-91BF-6961B7BBC15C",
        @Query("period_id") periodId: String = "1DAY",
        @Query("time_start") start: String = getDateBeforeDaysWithTime(700),
        @Query("limit") limitQuery: Int = 700
    ): CoinAPIResult

}