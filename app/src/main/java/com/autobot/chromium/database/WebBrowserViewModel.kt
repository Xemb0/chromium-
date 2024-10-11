package com.autobot.chromium.database

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebBrowserViewModel @Inject constructor(
    private val repository: TabRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val SELECTED_TAB_INDEX_KEY = "selected_tab_index"
    }

    private val _tabs = MutableStateFlow<List<TabData>>(emptyList())
    val tabs: StateFlow<List<TabData>> get() = _tabs

    private var selectedTabIndex: Int
        get() = savedStateHandle[SELECTED_TAB_INDEX_KEY] ?: 0
        set(value) {
            savedStateHandle[SELECTED_TAB_INDEX_KEY] = value
        }

    private val webViewMap = mutableMapOf<Int, WebViewHolder>()

    init {
        viewModelScope.launch {
            val savedTabs = repository.getAllTabs()
            _tabs.value =
                savedTabs

        }
    }

    private fun createInitialTab(): TabData {
        return TabData(
            name = "Home",
            url = "Home",
            icon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        )
    }
    fun updateUrl(newUrl: String) {
        val currentIndex = selectedTabIndex
        if (currentIndex in _tabs.value.indices) {
            _tabs.update { tabs ->
                tabs.mapIndexed { index, tab ->
                    if (index == currentIndex) tab.copy(url = newUrl) else tab
                }
            }
        }
    }


    fun selectTab(index: Int) {
        if (index in _tabs.value.indices) {
            selectedTabIndex = index
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
            _tabs.update { currentTabs -> currentTabs + tab }
        }
    }

    fun removeTab(tab: TabData) {
        viewModelScope.launch {
            repository.removeTab(tab)
            _tabs.update { currentTabs -> currentTabs - tab }
        }
    }

    fun getWebViewHolder(index: Int): WebViewHolder {
        return webViewMap.getOrPut(index) { WebViewHolder() }
    }

    fun updateWebViewHolder(index: Int, webViewHolder: WebViewHolder) {
        webViewMap[index] = webViewHolder
    }

    fun loadUrlInCurrentTab(newUrl: String) {
        val currentIndex = selectedTabIndex
        if (currentIndex in _tabs.value.indices) {
            val tab = _tabs.value[currentIndex]
            viewModelScope.launch {
                repository.loadUrlInTab(currentIndex, newUrl)
                updateUrl(newUrl) // Update the URL for the selected tab
            }
        }
    }
}