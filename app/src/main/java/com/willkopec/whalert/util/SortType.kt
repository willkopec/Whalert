package com.willkopec.whalert.util

enum class ChartType(val value: String){
    LINE("line"),
    CANDLE("candle"),
    BAR("bar"),
}

fun getAllTypes(): List<ChartType>{
    return listOf(ChartType.LINE, ChartType.CANDLE, ChartType.BAR)
}

fun getSortType(value: String): ChartType {
    val map = ChartType.values().associateBy(ChartType::value)
    return map[value]!!
}