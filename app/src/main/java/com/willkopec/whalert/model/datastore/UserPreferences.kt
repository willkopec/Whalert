package com.willkopec.whalert.model.datastore

data class UserPreferences(
    val darkMode: Boolean,
    val currentSymbol: String,
    val favoritesList: Set<String>
)
