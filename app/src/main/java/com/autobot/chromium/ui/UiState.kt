package com.autobot.chromium.ui

data class UiState(
    val selectedTabIndex: Int = 0,
    val showBottomSheet: Boolean = false,
    val textFieldValue: String = "",
    val clickSearch: Boolean = false
)