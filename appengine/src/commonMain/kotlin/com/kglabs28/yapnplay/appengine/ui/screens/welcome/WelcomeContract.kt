package com.kglabs28.yapnplay.appengine.ui.screens.welcome

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState

/** The first-launch welcome: the player's [name] and [avatarId] to keep (pre-filled with what they got). */
data class WelcomeState(
    val visible: Boolean = false,
    val name: String = "",
    val avatarId: String = "",
    val saving: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface WelcomeIntent : UiIntent {
    data class NameChanged(val name: String) : WelcomeIntent
    data class PickAvatar(val avatarId: String) : WelcomeIntent
    data object Save : WelcomeIntent
    data object Skip : WelcomeIntent
}

/** The welcome has no one-off effects. */
sealed interface WelcomeEffect : UiEffect
