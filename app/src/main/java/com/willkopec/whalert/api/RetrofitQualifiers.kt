package com.willkopec.whalert.api

import javax.inject.Qualifier

class RetrofitQualifiers {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CoinGeckoRetrofitInstance

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PolygonRetrofitInstance

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CoinAPIRetrofitInstance

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NewsAPIRetrofitInstance

}