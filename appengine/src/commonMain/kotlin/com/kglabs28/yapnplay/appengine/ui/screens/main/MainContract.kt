package com.kglabs28.yapnplay.appengine.ui.screens.main

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.navigation.AppNavTab

/**
 * Dashboard scaffold: shared top bar plus Home, Games and Rooms tabs.
 * Everything stays empty until [MainIntent.LoadProfile] fills it from
 * the repository — never hardcoded. Home tab cards switch tabs.
 */
data class MainState(
    val userName: String = "",
    val level: String = "",
    val online: Boolean = false,
    /** False until the first profile load lands; the top bar keeps its
     *  layout and only fades the values in, so nothing jumps. */
    val profileLoaded: Boolean = false,
    val coinBalance: String = "",
    val diamondBalance: String = "",
    val selectedTab: AppNavTab = AppNavTab.HOME,
) : UiState

sealed interface MainIntent : UiIntent {
    data class SelectTab(val tab: AppNavTab) : MainIntent
    data object LoadProfile : MainIntent
    data object OpenSettings : MainIntent
    data object OpenNotifications : MainIntent
}

sealed interface MainEffect : UiEffect {
    data object OpenSettingsDialog : MainEffect
    data object OpenNotificationsDialog : MainEffect
}
