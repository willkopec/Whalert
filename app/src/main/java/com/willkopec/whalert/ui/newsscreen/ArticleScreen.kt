package com.willkopec.whalert.ui.newsscreen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.model.newsAPI.Article

@SuppressLint("SetJavaScriptEnabled", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ArticleScreen(
    article: Article,
    viewModel: WhalertViewModel = hiltViewModel()
){

    var backEnabled by remember { mutableStateOf(true) }
    var webView: WebView? = null

    // Adding a WebView inside AndroidView
    // with layout as full screen
    Scaffold(
        topBar = {},
    ) {

        AndroidView(
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = WebViewClient()

                    // to play video on a web view
                    settings.javaScriptEnabled = true

                    webViewClient = object : WebViewClient() {

                        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                            backEnabled = view.canGoBack()
                        }

                    }

                    loadUrl(article.url)
                    webView = this
                }
            }, update = {
                webView = it
                //  it.loadUrl(url)
            })


        BackHandler(enabled = backEnabled) {
            webView?.goBack()
        }

    }

}