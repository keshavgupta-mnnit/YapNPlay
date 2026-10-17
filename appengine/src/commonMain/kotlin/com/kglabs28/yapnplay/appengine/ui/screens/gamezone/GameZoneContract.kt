package com.kglabs28.yapnplay.appengine.ui.screens.gamezone

import com.kglabs28.yapnplay.appengine.ui.components.games.GamePickerViewMode
import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem

/**
 * Game Zone tab: browse games (carousel or grid) and Quick Play the
 * focused one. Everything starts empty until [GameZoneIntent.Load].
 */
data class GameZoneState(
    val games: List<GameCatalogItem> = emptyList(),
    val focusedIndex: Int = 0,
    val viewMode: GamePickerViewMode = GamePickerViewMode.CAROUSEL,
    /** "1,248 Online"; empty until loaded. */
    val onlineLabel: String = "",
    val loaded: Boolean = false,
    /** Non-null while a Quick Play search is running or just found a match. */
    val search: QuickPlaySearchUi? = null,
    /** Shown under QUICK PLAY after a search ended without a match. */
    val searchMessage: String? = null,
) : UiState

/** A running search, already formatted for display. */
data class QuickPlaySearchUi(
    val headline: String,
    val progress: String,
    val timeLeft: String,
    val matchFound: Boolean,
)

sealed interface GameZoneIntent : UiIntent {
    data object Load : GameZoneIntent
    data class FocusGame(val index: Int) : GameZoneIntent
    data object ToggleViewMode : GameZoneIntent
    /** Start Quick Play for the game at [index] (carousel button or grid tile). */
    data class PlayGame(val index: Int) : GameZoneIntent
    data object CancelSearch : GameZoneIntent
}

sealed interface GameZoneEffect : UiEffect {
    data class OpenMatch(val matchId: String) : GameZoneEffect
}
