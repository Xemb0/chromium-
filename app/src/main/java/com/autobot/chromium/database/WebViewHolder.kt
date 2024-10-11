package com.autobot.chromium.database

import android.webkit.WebView


class WebViewHolder {
    var webView: WebView? = null
    var currentUrl: String = ""

//    // Optionally, you can include methods to initialize or clear the WebView
//    fun initializeWebView() {
//        webView = WebView(context).apply {
//            settings.javaScriptEnabled = true
//            loadUrl(currentUrl)
//        }
//    }

    fun clearWebView() {
        webView?.destroy()
        webView = null
    }
}