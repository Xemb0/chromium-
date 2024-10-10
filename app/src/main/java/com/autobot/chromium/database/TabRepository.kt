package com.autobot.chromium.database

interface TabRepository {
    suspend fun addTab(tab: TabData)
    suspend fun getAllTabs(): List<TabData>
    suspend fun deleteTab(tab: TabData)
    suspend fun removeTab(tab: TabData)
    suspend fun loadUrlInTab(selectedTabIndex: Int, newUrl: String)
    suspend fun getTabs(): List<TabData>
}