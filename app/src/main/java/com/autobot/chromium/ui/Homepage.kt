package com.autobot.chromium.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.autobot.chromium.database.TabData
import com.autobot.chromium.database.WebBrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    viewModel: WebBrowserViewModel = hiltViewModel(),
    onBottomSheetOptionClick: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var textFieldValue by remember { mutableStateOf("") }

    // Get the current URL from the ViewModel
    val currentUrl by viewModel.currentUrl
    val tabs by viewModel.tabs.collectAsState(initial = emptyList())

    // If no tabs are present, add the default Home tab
    LaunchedEffect(tabs) {
        if (tabs.isEmpty()) {
            viewModel.addTab("Home", "Home")
        } else {
            textFieldValue = currentUrl
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TabRow(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index, url ->
                selectedTabIndex = index
                viewModel.updateUrl(url)
                textFieldValue = url
            },
            onCloseTab = { index, tab ->
                viewModel.removeTab(tab)
                updateTabSelectionAfterClose(
                    tabs = tabs,
                    currentIndex = index,
                    onUpdateIndex = { newIndex -> selectedTabIndex = newIndex },
                    onUpdateUrl = { url ->
                        viewModel.updateUrl(url)
                        textFieldValue = url
                    }
                )
            }
        )

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            WebBrowser(
                url = currentUrl,
                onUrlChange = { newUrl ->
                    viewModel.updateUrl(newUrl)
                    textFieldValue = newUrl
                },
                modifier = Modifier.fillMaxHeight()
            )
        }


            SearchBarBrowser (
                textFieldValue = textFieldValue,
                onTextFieldValueChange = { newValue ->
                    textFieldValue = newValue
                },
                onReload = {
                    viewModel.getWebViewHolder(selectedTabIndex).webView?.reload()
                },
                onSearch = {
                    viewModel.updateUrl(textFieldValue)
                    viewModel.loadUrlInCurrentTab(selectedTabIndex, textFieldValue)
                },
                onMenuClick = {
                    showBottomSheet = true
                },
                onFocusChange = {
                    viewModel.updateUrl(textFieldValue)
                },
                onAddTab = {
                    viewModel.addTab("New Tab", textFieldValue)
                },
                onSuggestionClick = {
                    viewModel.updateUrl(it)
                    viewModel.loadUrlInCurrentTab(selectedTabIndex, it)
                },
                suggestions = listOf("https://google.com", "https://www.youtube.com")
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