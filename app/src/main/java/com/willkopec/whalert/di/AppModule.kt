package com.willkopec.whalert.di

import com.willkopec.whalert.api.RetrofitInstance
import com.willkopec.whalert.api.RetrofitQualifiers
import com.willkopec.whalert.util.Constants.Companion.BASE_URL
import com.willkopec.whalert.util.Constants.Companion.COIN_API_BASE_URL
import com.willkopec.whalert.util.Constants.Companion.POLYGON_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    @RetrofitQualifiers.CoinGeckoRetrofitInstance
    fun provideCoinGeckoRetrofitInstance(): RetrofitInstance {
        return RetrofitInstance.getInstance(BASE_URL)
    }

    @Provides
    @Singleton
    @RetrofitQualifiers.PolygonRetrofitInstance
    fun providePolygonRetrofitInstance(): RetrofitInstance {
        return RetrofitInstance.getInstance(POLYGON_BASE_URL)
    }

    @Provides
    @Singleton
    @RetrofitQualifiers.CoinAPIRetrofitInstance
    fun provideCoinAPIRetrofitInstance(): RetrofitInstance {
        return RetrofitInstance.getInstance(COIN_API_BASE_URL)
    }

}