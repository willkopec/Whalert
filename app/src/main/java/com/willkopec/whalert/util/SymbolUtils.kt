package com.willkopec.whalert.util

object SymbolUtils {

    fun convertToCoingeckoApiFormat(symbol: String): String {
        return "X:${symbol}USD"
    }

    fun convertToCoinAPIFormat(symbol: String): String {
        return "BITSTAMP_SPOT_${symbol}_USD"
    }

}