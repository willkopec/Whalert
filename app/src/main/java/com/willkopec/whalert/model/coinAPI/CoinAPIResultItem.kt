package com.willkopec.whalert.model.coinAPI

data class CoinAPIResultItem(
    val price_close: Double,
    val price_high: Double,
    val price_low: Double,
    val price_open: Double,
    val time_close: String,
    val time_open: String,
    val time_period_end: String,
    val time_period_start: String,
    val trades_count: Int,
    val volume_traded: Double,
    val current_sma1: Double? = null,
    val current_sma2: Double? = null,
    var current_risk: Double? = null
)