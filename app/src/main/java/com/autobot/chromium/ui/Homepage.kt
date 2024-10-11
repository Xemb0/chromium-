package com.autobot.chromium.ui
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autobot.chromium.database.TabData
import com.autobot.chromium.database.WebBrowserViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    viewModel: WebBrowserViewModel = hiltViewModel(),
    onBottomSheetOptionClick: (String) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var textFieldValue by rememberSaveable { mutableStateOf("") }

    val tabs by viewModel.tabs.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    // Ensure a default tab is added if no tabs are present
    LaunchedEffect(tabs.size) {
        if (tabs.isEmpty()) {
            viewModel.addTab("Home", "")
        }
    }

    Column(modifier = Modifier.fillMaxSize().border(2.dp, color = androidx.compose.ui.graphics.Color.Black)) {

        // TabRow using pagerState to reflect current tab
        TabRow(
            tabs = tabs,
            selectedTabIndex = pagerState.currentPage,
            onTabSelected = { index, url ->
                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                viewModel.updateUrl(url)
                textFieldValue = url
            },
            onCloseTab = { index, tab ->
                viewModel.removeTab(tab)
                updateTabSelectionAfterClose(
                    tabs = tabs,
                    currentIndex = index,
                    onUpdateIndex = { newIndex ->
                        coroutineScope.launch { pagerState.scrollToPage(newIndex) }
                    },
                    onUpdateUrl = { url ->
                        viewModel.updateUrl(url)
                        textFieldValue = url
                    }
                )
            }
        )
Row {

        // HorizontalPager to handle switching between tab content
        HorizontalPager(
            count = tabs.size,
            state = pagerState,
            modifier = Modifier.weight(1f).border(2.dp, color = androidx.compose.ui.graphics.Color.Black)
        ) { page ->
            val webViewHolder = viewModel.getWebViewHolder(page)
            if (tabs[page].url == "Home") {
                BrowserHomePage { url ->
                    viewModel.updateUrl(url)
                    textFieldValue = url
                }
            } else {
                WebBrowser(
                    webViewHolder = webViewHolder,
                    onUrlChange = { newUrl ->
                        viewModel.updateUrl(newUrl)
                        textFieldValue = newUrl
                    },
                )
            }
        }
    }

        // Search bar for URL handling, search, reload, etc.
        SearchBarBrowser(
            textFieldValue = textFieldValue,
            onTextFieldValueChange = { newValue -> textFieldValue = newValue },
            onReload = {
                viewModel.getWebViewHolder(pagerState.currentPage).webView?.reload()
            },
            onSearch = {
                if (textFieldValue.isNotBlank()) {
                    viewModel.updateUrl(textFieldValue)
                    viewModel.loadUrlInCurrentTab(textFieldValue)
                }
            },
            onMenuClick = { showBottomSheet = true },
            onAddTab = {
                viewModel.addTab("New Tab", "Home")
                coroutineScope.launch { pagerState.scrollToPage(tabs.size) }
            },
            suggestions = listOf("https://google.com", "https://www.youtube.com"),
            onFocusChange = {},
            onSuggestionClick = { suggestion ->
                viewModel.updateUrl(suggestion)
                textFieldValue = suggestion
            }
        )
    }

    // ModalBottomSheet for menu options
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            BottomSheetContent(onBottomSheetOptionClick = onBottomSheetOptionClick)
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
            .padding(top = 8.dp, start = 8.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            TabItem(
                tabData = tab,
                isSelected = index == selectedTabIndex,
                onClick = { onTabSelected(index, tab.url) },
                modifier = Modifier
                    .width(160.dp)
                    .padding(end = 8.dp),
                onCloseClick = { onCloseTab(index, tab) }
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
    val newIndex = when {
        tabs.isEmpty() -> -1
        currentIndex >= tabs.size -> tabs.lastIndex
        else -> currentIndex.coerceIn(0, tabs.size - 1)
    }
    onUpdateIndex(newIndex)
    onUpdateUrl(if (newIndex >= 0) tabs[newIndex].url else "Home")
}
