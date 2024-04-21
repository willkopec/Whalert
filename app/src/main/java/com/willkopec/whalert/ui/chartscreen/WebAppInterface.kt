package com.willkopec.whalert.ui.chartscreen

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.willkopec.whalert.breakingnews.WhalertViewModel

class WebAppInterface(private val context: Context, private val viewModel: WhalertViewModel) {
    // Method to handle adding symbol to favorites
    @JavascriptInterface
    fun addToFavorites(symbol: String) {
        // Here you can add the symbol to your ViewModel or perform any other action in your app
        // For example:
        viewModel.addSymbolToSaved(symbol)
        Toast.makeText(context, "Added to favorites: $symbol", Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun deleteFromFavorites(symbol: String) {
        // Here you can add the symbol to your ViewModel or perform any other action in your app
        // For example:
        viewModel.deleteSymbolFromSaved(symbol)
        Toast.makeText(context, "Deleted to favorites: $symbol", Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun isInFavorites(symbol: String): Boolean {
        return viewModel.isInFavoritesList(symbol)
    }

}