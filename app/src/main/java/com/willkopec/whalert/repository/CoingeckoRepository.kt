package com.willkopec.whalert.repository

import android.util.Log
import com.willkopec.whalert.api.CoingeckoAPI
import com.willkopec.whalert.api.RetrofitInstance
import com.willkopec.whalert.model.coingecko.CryptoResponse
import com.willkopec.whalert.util.Constants.Companion.BASE_URL
import com.willkopec.whalert.util.Resource
import javax.inject.Inject

//https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd

class CoingeckoRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {

    suspend fun getCryptoList(pageNumber: Int): Resource<CryptoResponse> {
        val coingeckoApiService = retrofitInstance.createService(CoingeckoAPI::class.java)

        val response = try {
            coingeckoApiService.getTopCryptos()
        } catch (e: Exception) {
            Log.d("CoingeckoRepo", e.printStackTrace().toString())
            return Resource.Error("An unknown error occurred!")
        }
        return Resource.Success(response)
    }
}