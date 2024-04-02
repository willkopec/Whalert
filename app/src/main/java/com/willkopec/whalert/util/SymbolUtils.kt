package com.willkopec.whalert.util

object SymbolUtils {

    fun convertToApiSymbolString(symbol: String): String {
        return "X:${symbol}USD"
    }

}