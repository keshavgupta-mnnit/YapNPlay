package com.kglabs28.yapnplay.appengine.features.about.ui

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.features.about.domain.AppInfo

/** About screen contract: state, intents and one-off effects. */
data class AboutScreenState(
    val loading: Boolean = true,
    val info: AppInfo? = null,
    val error: String? = null,
) : UiState

sealed interface AboutScreenIntent : UiIntent {
    data object Load : AboutScreenIntent
    data object Retry : AboutScreenIntent
    data object GoBack : AboutScreenIntent
}

sealed interface AboutScreenEffect : UiEffect {
    data object Back : AboutScreenEffect
}
