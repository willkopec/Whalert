package com.willkopec.mvvmnewsappincompose.util

import android.content.Context
import android.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPreference @Inject constructor(@ApplicationContext context : Context){
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun isDarkMode(): Boolean {
        return prefs.getBoolean("darkMode", false)!!
    }

    fun switchDarkMode() {
        prefs.edit().putBoolean("darkMode", !isDarkMode()).apply()
    }

}