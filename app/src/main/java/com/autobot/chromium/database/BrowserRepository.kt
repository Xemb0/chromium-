package com.autobot.chromium.database

import javax.inject.Inject

class BrowserRepository @Inject constructor(private val tabDao: TabDao) : TabRepository {
    override suspend fun addTab(tab: TabData) {
        tabDao.insert(tab)
    }

    override suspend fun getAllTabs(): List<TabData> {
        return tabDao.getAllTabs()
    }

    override suspend fun deleteTab(tab: TabData) {
        tabDao.delete(tab)
    }

    override suspend fun removeTab(tab: TabData) {
        tabDao.removeById(tab.id)
    }

    override suspend fun loadUrlInTab(selectedTabIndex: Int, newUrl: String) {
        tabDao.changeUrl(selectedTabIndex, newUrl)
    }

    private val tabs = mutableListOf<TabData>()

    override suspend fun getTabs(): List<TabData> {
        return tabs.toList()
    }
}