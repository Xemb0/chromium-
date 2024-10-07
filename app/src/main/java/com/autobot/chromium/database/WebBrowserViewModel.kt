package com.autobot.chromium.database

import android.graphics.Bitmap
import android.webkit.WebView
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

    fun removeTab(tab: TabData) {
        viewModelScope.launch {
            repository.removeTab(tab)
            _tabs.update { currentTabs ->
                currentTabs - tab
            }
        }
    }

    fun getWebViewHolder(index: Int): WebViewHolder {
        return webViewMap.getOrPut(index) { WebViewHolder() }
    }

    fun updateWebViewHolder(index: Int, webViewHolder: WebViewHolder) {
        webViewMap[index] = webViewHolder
    }

    fun loadUrlInCurrentTab(selectedTabIndex: Int, newUrl: String) {
        viewModelScope.launch {
            repository.loadUrlInTab(selectedTabIndex,newUrl)
        }


    }
}

class WebViewHolder(var webView: WebView? = null, var currentUrl: String? = null)