package com.willkopec.whalert.util

import android.util.Log
import com.willkopec.whalert.model.coinAPI.CoinAPIResultItem
import kotlin.math.ln

object IndicatorUtil {

    fun getRiskLevel(list: List<CoinAPIResultItem>): Double {
        var maxValue: Double = 1.0
        var maxValueDate: String = ""
        var sma1 : Double = 0.0
        var sma2 : Double = 0.0

        list.forEach {
            val ratio = it.current_sma1?.div(it.current_sma2 ?: 1.0) ?: 0.0
            if (ratio > maxValue && ratio < 4.1) {
                maxValue = ratio
                maxValueDate = it.time_period_end
                sma1= it.current_sma1!!
                sma2= it.current_sma2!!
            }
        }

        // Logarithmic regression model for BTC with diminishing returns
        val fairValue: Double = ( ((-17.597) + 4.191 * ln(list.size.toDouble()/7)) * 0.58 + ((-9.4829) + 3.1838 * ln(list.size.toDouble()/7)) * 0.42)

        Log.d("Utils", "Date: $maxValueDate sma1: $sma1 sma2: $sma2 MAX VALUE : $maxValue")
        val smaRatio: Double = list.get(0).current_sma1?.div(list.get(0).current_sma2!!) ?: 0.0
        val smaRatioNormalized: Double = smaRatio / (maxValue * 1.15)

        return (smaRatioNormalized - 0.2) * (fairValue / 5) + 0.2
    }

}