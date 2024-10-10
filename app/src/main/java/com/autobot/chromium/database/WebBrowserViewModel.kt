package com.autobot.chromium.database

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebBrowserViewModel @Inject constructor(private val repository: TabRepository) : ViewModel() {
    // StateFlow to hold the list of tabs
    private val _tabs = MutableStateFlow<List<TabData>>(emptyList())
    val tabs: StateFlow<List<TabData>> get() = _tabs

    // Current URL for the selected tab
    private val _currentUrl = mutableStateOf("Home")
    val currentUrl: State<String> = _currentUrl

    private val webViewMap = mutableMapOf<Int, WebViewHolder>()

    init {
        viewModelScope.launch {
            val savedTabs = repository.getAllTabs()
            if (savedTabs.isEmpty()) {
                // Add the initial tab if no saved tabs are found
                val initialTab = TabData(
                    name = "Home",
                    url = "Home",
                    icon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                )
                repository.addTab(initialTab)
                _tabs.value = listOf(initialTab)
            } else {
                _tabs.value = savedTabs
            }
        }
    }

    // Function to update the current URL in the state
    fun updateUrl(newUrl: String) {
        _currentUrl.value = newUrl
        val currentTabIndex = _tabs.value.indexOfFirst { it.url == _currentUrl.value }
        webViewMap[currentTabIndex]?.currentUrl = newUrl
    }

    // Function to add a new tab
    fun addTab(name: String, url: String) {
        val tab = TabData(
            name = name,
            url = url,
            icon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        )
        viewModelScope.launch {
            repository.addTab(tab)
            _tabs.update { currentTabs ->
                currentTabs + tab
            }
        }
    }

    // Function to remove a tab
    fun removeTab(tab: TabData) {
        viewModelScope.launch {
            repository.removeTab(tab)
            _tabs.update { currentTabs ->
                currentTabs - tab
            }
        }
    }

    // Retrieve or create a WebViewHolder for a given tab index
    fun getWebViewHolder(index: Int): WebViewHolder {
        return webViewMap.getOrPut(index) { WebViewHolder() }
    }

    // Update WebViewHolder with new data
    fun updateWebViewHolder(index: Int, webViewHolder: WebViewHolder) {
        webViewMap[index] = webViewHolder
    }

    // Load a new URL in the currently selected tab
    fun loadUrlInCurrentTab(selectedTabIndex: Int, newUrl: String) {
        viewModelScope.launch {
            val tab = _tabs.value.getOrNull(selectedTabIndex)
            tab?.let {
                repository.loadUrlInTab(selectedTabIndex, newUrl)
                updateUrl(newUrl)
            }
        }
    }
}
