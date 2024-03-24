package com.willkopec.whalert.repository

import com.willkopec.whalert.api.RetrofitInstance
import com.willkopec.whalert.model.CryptoResponse
import com.willkopec.whalert.util.Resource
import javax.inject.Inject

//https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd

class CoingeckoRepository @Inject constructor(

) {

    suspend fun getBreakingNews(pageNumber: Int) : Resource<CryptoResponse> {
        val response = try {
            RetrofitInstance.api.getBreakingNews()
        } catch (e: Exception){
            return Resource.Error("An unknown error occured!")
        }
        return Resource.Success(response)
    }

}