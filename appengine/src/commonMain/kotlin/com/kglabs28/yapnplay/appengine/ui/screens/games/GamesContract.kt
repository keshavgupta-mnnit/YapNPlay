package com.kglabs28.yapnplay.appengine.ui.screens.games

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Games tab. Copy lives in [Strings]; the view model only re-asserts
 * it until real game-list data arrives.
 */
data class GamesState(
    val title: String = Strings.gamesTabTitle,
    val subtitle: String = Strings.gamesTabSubtitle,
) : UiState

sealed interface GamesIntent : UiIntent {
    data object Refresh : GamesIntent
}

sealed interface GamesEffect : UiEffect
