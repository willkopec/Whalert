package com.willkopec.whalert.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap

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

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }

    companion object {

        private val instances: MutableMap<String, RetrofitInstance> = HashMap()

        fun getInstance(baseUrl: String): RetrofitInstance {
            return instances.getOrPut(baseUrl) { RetrofitInstance(baseUrl) }
        }
    }
}