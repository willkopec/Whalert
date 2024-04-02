package com.willkopec.whalert.model.coingecko

data class Roi(
    val currency: String,
    val percentage: Double,
    val times: Double
)