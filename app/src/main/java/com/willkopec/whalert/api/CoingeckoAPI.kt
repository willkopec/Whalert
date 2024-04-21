package com.willkopec.whalert.api

import com.willkopec.whalert.model.coingecko.CryptoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//https://rest.coinapi.io/v1/ohlcv/BINANCE_SPOT_ETH_BTC/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1DAY&time_start=2023-03-01T00:00:00&limit=1000

//https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_DOGE_USD/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1DAY&time_start=2020-08-01T00:00:00&limit=2000
interface CoingeckoAPI {

    @GET("api/v3/coins/markets")
    suspend fun getTopCryptos(
        @Query("vs_currency")
        countryCode: String = "usd"
    ): CryptoResponse


}