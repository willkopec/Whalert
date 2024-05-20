package com.willkopec.whalert.repository

import com.willkopec.whalert.api.NewsAPI
import com.willkopec.whalert.api.RetrofitInstance
import com.willkopec.whalert.model.newsAPI.NewsResponse
import com.willkopec.whalert.util.Resource
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val retrofitInstance: RetrofitInstance
) {

    suspend fun getCryptoNews(): Resource<NewsResponse> {

        val newsApiService = retrofitInstance.createService(NewsAPI::class.java)

        val response = try {
            newsApiService.getCryptoNews()
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred!")
        }
        return Resource.Success(response)
    }

}