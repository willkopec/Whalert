package com.willkopec.whalert.api

import com.willkopec.whalert.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance private constructor(baseUrl: String) {

    private val retrofit: Retrofit

    init {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    companion object {

        @Volatile
        private var instance: RetrofitInstance? = null

        fun getInstance(baseUrl: String): RetrofitInstance {
            return instance ?: synchronized(this) {
                instance ?: RetrofitInstance(baseUrl).also { instance = it }
            }
        }
    }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }
}