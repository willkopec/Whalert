package com.willkopec.whalert.api

import javax.inject.Qualifier

class RetrofitQualifiers {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CoinGeckoRetrofitInstance

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PolygonRetrofitInstance

}