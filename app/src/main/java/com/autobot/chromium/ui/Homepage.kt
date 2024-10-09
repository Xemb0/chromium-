package com.autobot.chromium.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.autobot.chromium.database.TabData
import com.autobot.chromium.database.WebBrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(viewModel: WebBrowserViewModel = hiltViewModel(),onBottomSheetOptionClick: (String) -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var currentUrl by remember { mutableStateOf("Home") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isSearchBarFocused by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Collecting tabs directly from the Flow
    val tabs by viewModel.tabs.collectAsState(initial = emptyList())

    // If no tabs are present, add the default Home tab
    LaunchedEffect(tabs) {
        if (tabs.isEmpty()) {
            viewModel.addTab("Home", "Home")
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TabRow(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index, url ->
                selectedTabIndex = index
                currentUrl = url
            },
            onCloseTab = { index, tab ->
                viewModel.removeTab(tab)
                updateTabSelectionAfterClose(tabs, index, { selectedTabIndex = it }, { currentUrl = it })

                selectedTabIndex -= 1

            }
        )

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            DisplayWebView(
                viewModel = viewModel,
                selectedTabIndex = selectedTabIndex,
                currentUrl = currentUrl,
                onUrlChanged = { url -> currentUrl = url }
            )
        }

        // Bottom Search Bar with add, search, and menu options
        SearchBarBrowser(
            onAddTab = {
                viewModel.addTab("Tab ${tabs.size + 1}", "Home")
                selectedTabIndex = tabs.size - 1
                currentUrl = "Home"
            },
            onSearch = { query ->
                val newUrl = "https://www.google.com/search?q=$query"
                viewModel.loadUrlInCurrentTab(selectedTabIndex, newUrl)
                currentUrl = newUrl
            },
            onMenuClick = {
                showBottomSheet = true
            },
            onTextChange = { text ->
                currentUrl = text
            },
            onFocusChange = { focused ->
                isSearchBarFocused = focused
            },
            searchBarText = currentUrl,
            suggestions = if (isSearchBarFocused && currentUrl.isNotEmpty()) {
                listOf("Compose", "Jetpack", "Android", "Kotlin")
            } else {
                emptyList()
            },
            onSuggestionClick = { suggestion ->
                val newUrl = "https://www.google.com/search?q=$suggestion"
                viewModel.loadUrlInCurrentTab(selectedTabIndex, newUrl)
                currentUrl = newUrl
            }
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            modifier = Modifier
        ) {
            BottomSheetContent(
                onBottomSheetOptionClick = {
                    onBottomSheetOptionClick(it)
                }

            )
        }
    }
}
@Composable
fun TabRow(
    tabs: List<TabData>,
    selectedTabIndex: Int,
    onTabSelected: (Int, String) -> Unit,
    onCloseTab: (Int, TabData) -> Unit
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            TabItem(
                tabData = tab,
                isSelected = index == selectedTabIndex,
                onClick = { onTabSelected(index, tab.url) },
                modifier = Modifier.width(160.dp).padding(end = 8.dp),
                onCloseClick = { onCloseTab(index, tab)
                }
            )
        }
    }
}

@Composable
fun DisplayWebView(
    viewModel: WebBrowserViewModel,
    selectedTabIndex: Int,
    currentUrl: String,
    onUrlChanged: (String) -> Unit
) {
    val webViewHolder = viewModel.getWebViewHolder(selectedTabIndex)
    val tabs = viewModel.tabs.collectAsStateWithLifecycle().value

    if (currentUrl == "Home" || tabs.isEmpty()) {
        BrowserHomePage {
            viewModel.addTab("Tab ${tabs.size + 1}", "Home")
        }
    } else {
        WebViewContainer(
            url = currentUrl,
            onWebViewCreated = { webView ->
                webViewHolder.webView = webView
            },
            onUrlChanged = { url ->
                webViewHolder.currentUrl = url
                onUrlChanged(url)
            }
        )
    }
}

@Composable
fun WebViewContainer(url: String, onWebViewCreated: (WebView) -> Unit, onUrlChanged: (String) -> Unit) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                loadUrl(url)
                onWebViewCreated(this)
            }
        },
        update = { webView ->
            if (url != webView.url) {
                webView.loadUrl(url)
            }

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    url?.let { onUrlChanged(it) }
                }
            }
        }
    )
}

private fun updateTabSelectionAfterClose(
    tabs: List<TabData>,
    currentIndex: Int,
    onUpdateIndex: (Int) -> Unit,
    onUpdateUrl: (String) -> Unit
) {
    val newIndex = if (tabs.isNotEmpty()) {
        currentIndex.coerceIn(0, tabs.size - 1)
    } else {
        -1
    }
    onUpdateIndex(newIndex)
    onUpdateUrl(if (newIndex >= 0) tabs[newIndex].url else "Home")
}