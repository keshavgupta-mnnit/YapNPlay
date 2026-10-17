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
    /** Bell history, newest first (at most 10). */
    val notifications: List<NotificationUi> = emptyList(),
    val unreadNotifications: Int = 0,
    val notificationsOpen: Boolean = false,
) : UiState

/** One row of the bell's drop-down. */
data class NotificationUi(
    val id: String,
    val roomId: String,
    val title: String,
    val body: String,
    val timeLabel: String,
    val unread: Boolean,
)

sealed interface MainIntent : UiIntent {
    data class SelectTab(val tab: AppNavTab) : MainIntent
    data object LoadProfile : MainIntent
    data object OpenSettings : MainIntent
    /** Bell tapped: opens or closes the drop-down. */
    data object ToggleNotifications : MainIntent
    /** Drop-down closed by tapping outside or back. */
    data object DismissNotifications : MainIntent
    data class NotificationClicked(val roomId: String) : MainIntent
}

sealed interface MainEffect : UiEffect {
    data object OpenSettingsDialog : MainEffect
    data class OpenRoom(val roomId: String) : MainEffect
}
