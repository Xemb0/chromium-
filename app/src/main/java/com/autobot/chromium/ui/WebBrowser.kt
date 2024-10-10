package com.autobot.chromium.ui

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    val state = rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()

    Column(modifier = modifier) {
        val loadingState = state.loadingState
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                progress = loadingState.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // A custom WebViewClient can be provided via subclassing
        val webClient = remember {
            object : AccompanistWebViewClient() {
                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.d("Accompanist WebView", "Page started loading for $url")
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
        state.content.getCurrentUrl()?.let { onUrlChange(it) }
    }
}
@Composable
fun WebSearchBar(
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateForward: () -> Unit,
    onReload: () -> Unit,
    onGo: () -> Unit
) {
    Row {

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onReload) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
            IconButton(onClick = onGo) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Go"
                )
            }
        }
    }

    Row(modifier = Modifier.padding(all = 12.dp)) {
        BasicTextField(
            modifier = Modifier.weight(9f),
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            maxLines = 1
        )
        // Add any additional UI here, like error icons or indicators.
    }
}