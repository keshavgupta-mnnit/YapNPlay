package com.kglabs28.yapnplay.appengine.ui.screens.home

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend

data class HomeState(
    val userName: String = "",
    val level: String = "",
    val levelProgressText: String = "",
    val coinBalance: String = "",
    val diamondBalance: String = "",
    val friends: List<Friend> = emptyList(),
) : UiState

sealed interface HomeIntent : UiIntent {
    data object LoadProfile : HomeIntent
    data object EnterGameZone : HomeIntent
    data object PlayWithFriends : HomeIntent
    data object OpenSettings : HomeIntent
    data object OpenNotifications : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data object NavigateToGameZone : HomeEffect
    data object NavigateToPlayWithFriends : HomeEffect
    data object OpenSettingsDialog : HomeEffect
    data object OpenNotificationsDialog : HomeEffect
}
