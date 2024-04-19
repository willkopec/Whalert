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
        @Path("apiKey") apiKey: String = "apikey-59659DAF-46F7-4981-BCDB-6A10B727341E",
        @Query("period_id") periodId: String = "1DAY",
        @Query("time_start") start: String = getDateBeforeDaysWithTime(700),
        @Query("limit") limitQuery: Int = 700
    ): CoinAPIResult

}