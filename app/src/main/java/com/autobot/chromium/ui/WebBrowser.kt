package com.autobot.chromium.ui

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebBrowser(
    url: String,
    onUrlChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberWebViewState(url = filterUrl(url))
    val navigator = rememberWebViewNavigator()

    Column(modifier = modifier) {
        val loadingState = state.loadingState
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                progress = loadingState.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        val webClient = remember {
            object : AccompanistWebViewClient() {
                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.d("Accompanist WebView", "Page started loading for $url")

                    // Validate or filter the URL before continuing.
                    url?.let {
                        if (!isValidUrl(it)) {
                            // If the URL is not valid, stop loading or redirect.
                            navigator.loadUrl("https://www.google.com")
                        }
                    }
                }
            }
        }

        WebView(
            state = state,
            modifier = Modifier.weight(1f),
            navigator = navigator,
            onCreated = { webView ->
                webView.settings.javaScriptEnabled = true
            },
            client = webClient
        )
    }

    // Update the URL in the parent whenever the WebView state changes
    LaunchedEffect(state.content.getCurrentUrl()) {
        state.content.getCurrentUrl()?.let { newUrl ->
            // Only propagate valid URLs back.
            if (isValidUrl(newUrl)) {
                onUrlChange(newUrl)
            }
        }
    }
}

// Helper function to filter or format the URL
private fun filterUrl(url: String): String {
    // Use formatUrl or any additional filtration logic
    return if (url.contains(".") && !url.contains(" ")) {
        formatUrl(url)
    } else {
        "https://www.google.com/search?q=${url.replace(" ", "+")}"
    }
}

// Helper function to validate the URL
private fun isValidUrl(url: String): Boolean {
    // Ensure the URL is not empty and starts with "http" or "https"
    return url.startsWith("http://") || url.startsWith("https://")
}