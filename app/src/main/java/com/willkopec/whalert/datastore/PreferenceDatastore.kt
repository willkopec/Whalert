package com.willkopec.whalert.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.willkopec.whalert.model.datastore.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("settings")
class PreferenceDatastore @Inject constructor(@ApplicationContext context : Context) {


    var pref = context.dataStore
    private val darkMode =
        booleanPreferencesKey("DARK_MODE")
    private val currentSymbol =
        stringPreferencesKey("CURRENT_SYMBOL")
    private val favoritesList =
        stringSetPreferencesKey("FAVORITES_LIST")

    suspend fun setDarkMode(value: Boolean){
        pref.edit {
            it[darkMode] = value
        }
    }

    suspend fun setCurrentSymbol(symbol: String){
        pref.edit {
            it[currentSymbol] = symbol
        }
    }

    suspend fun isSymbolInFavorites(symbol: String): Boolean {
        val userPreferences = getDetails().first()
        return symbol in userPreferences.favoritesList
    }

    suspend fun addToFavoritesList(element: String) {
        pref.updateData { preferences ->
            val currentFavorites = preferences[favoritesList] ?: emptySet()
            val newFavorites = currentFavorites.toMutableSet()
            newFavorites.add(element)
            preferences.toMutablePreferences().apply {
                this[favoritesList] = newFavorites
            }
        }
    }

    suspend fun deleteFromFavoritesList(element: String) {
        pref.updateData { preferences ->
            val currentFavorites = preferences[favoritesList] ?: emptySet()
            val newFavorites = currentFavorites.toMutableSet()
            newFavorites.remove(element)
            preferences.toMutablePreferences().apply {
                this[favoritesList] = newFavorites
            }
        }
    }

    fun getDetails() = pref.data.map {
        UserPreferences(
            darkMode = it[darkMode]?:false,
            currentSymbol = it[currentSymbol]?:"BTC",
            favoritesList = it[favoritesList] ?: emptySet()
        )
    }

}