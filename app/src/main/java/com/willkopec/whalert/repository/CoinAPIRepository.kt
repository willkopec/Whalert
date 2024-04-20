package com.willkopec.whalert.repository

import com.willkopec.whalert.api.CoinAPI
import com.willkopec.whalert.api.RetrofitInstance
import com.willkopec.whalert.model.coinAPI.CoinAPIResult
import com.willkopec.whalert.util.Resource
import javax.inject.Inject

class CoinAPIRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {

    suspend fun getSymbolData(symbol: String, start: String, limit: Int): Resource<CoinAPIResult> {
        val coinApiService = retrofitInstance.createService(CoinAPI::class.java)

        val response = try {
            coinApiService.getSymbolData(symbol)
        } catch (e: Exception) {
            return Resource.Error("Unknown symbol or No Network Connection!")
        }
        return Resource.Success(response)
    }
}