package com.willkopec.mvvmnewsappincompose.util

enum class SortType(val value: String){
    BREAKING("Breaking News"),
    ECONOMIC("Economic News"),
    SPORTS("Sports News"),
    HEALTH("Health News")
}

fun getAllTypes(): List<SortType>{
    return listOf(SortType.BREAKING, SortType.ECONOMIC, SortType.SPORTS, SortType.HEALTH)
}

fun getSortType(value: String): SortType {
    val map = SortType.values().associateBy(SortType::value)
    return map[value]!!
}