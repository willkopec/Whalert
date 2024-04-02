package com.willkopec.whalert.api

import com.willkopec.whalert.model.polygon.PolygonResponse
import com.willkopec.whalert.util.Constants.Companion.POLYGON_API
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PolygonAPI {

    @GET("v2/aggs/ticker/{symbol}/range/1/day/{from}/{to}")
    suspend fun getTopCryptos(
        @Path("symbol") symbol: String,
        @Path("from") from: String,
        @Path("to") to: String,
        @Query("apiKey") api: String = POLYGON_API
    ): PolygonResponse

}