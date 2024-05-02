package com.willkopec.whalert.util

object SymbolUtils {

    fun convertToCoingeckoApiFormat(symbol: String): String {
        return "X:${symbol}USD"
    }

    fun convertToCoinAPIFormat(symbol: String, exchange: String = "KRAKEN", vsCurrency: String = "USD"): String {
        return "${exchange}_SPOT_${symbol}_${vsCurrency}"
    }

}