package com.kglabs28.yapnplay.appengine.ui.screens.gamezone

import com.kglabs28.yapnplay.appengine.ui.components.coins.LeaguePickerUi
import com.kglabs28.yapnplay.appengine.ui.components.coins.CoinsOfferUi
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
    /** Play is blocked after a recorded incident; offers a rewarded ad. */
    val penalty: PenaltyUi? = null,
    /** Non-null while the league picker is open (before Quick Play). */
    val leagues: LeaguePickerUi? = null,
    /** Short of a league's fee: watch an ad for coins or play Starter. */
    val coinsOffer: CoinsOfferUi? = null,
) : UiState

/** The user's play block, ready to show. */
data class PenaltyUi(val title: String, val body: String, val watchingAd: Boolean)

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
    /** Quick Play for the game at [index] (carousel button or grid tile): opens the league picker. */
    data class PlayGame(val index: Int) : GameZoneIntent
    data object CancelSearch : GameZoneIntent
    data object WatchAd : GameZoneIntent
    data object DismissPenalty : GameZoneIntent
    /** A league row tapped: search in it, or the coins offer when it's locked. */
    data class PickLeague(val entryCoins: Int) : GameZoneIntent
    data object CloseLeagues : GameZoneIntent
    data object WatchAdForCoins : GameZoneIntent
    /** From the coins offer: play the free Starter league. */
    data object PlayFree : GameZoneIntent
    data object CloseCoinsOffer : GameZoneIntent
}

sealed interface GameZoneEffect : UiEffect {
    data class OpenMatch(val gameId: String, val matchId: String) : GameZoneEffect
}
