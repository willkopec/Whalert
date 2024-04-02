package com.willkopec.whalert.repository

import com.willkopec.whalert.api.CoingeckoAPI
import com.willkopec.whalert.api.PolygonAPI
import com.willkopec.whalert.api.RetrofitInstance
import com.willkopec.whalert.model.coingecko.CryptoResponse
import com.willkopec.whalert.model.polygon.PolygonResponse
import com.willkopec.whalert.util.Constants
import com.willkopec.whalert.util.Resource
import javax.inject.Inject

class PolygonRepository @Inject constructor(

) {

    suspend fun getSymbolData(symbol: String, from: String, to: String): Resource<PolygonResponse> {
        val polygonRetrofit = RetrofitInstance.getInstance(Constants.POLYGON_BASE_URL)
        val polygonApiService = polygonRetrofit.createService(PolygonAPI::class.java)

        val response = try {
            polygonApiService.getTopCryptos(symbol,from,to)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred!")
        }
        return Resource.Success(response)
    }

}