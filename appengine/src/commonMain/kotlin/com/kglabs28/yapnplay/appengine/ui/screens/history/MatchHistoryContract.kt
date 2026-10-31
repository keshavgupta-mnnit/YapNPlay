package com.kglabs28.yapnplay.appengine.ui.screens.history

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi

/** The user's past matches as rows; [board] is the leaderboard of the one they opened. */
data class MatchHistoryState(
    val loaded: Boolean = false,
    val error: String? = null,
    val rows: List<MatchHistoryRowUi> = emptyList(),
    val board: LeaderboardUi? = null,
    /** The opened match's game (its artwork on the leaderboard). */
    val boardGameId: String = "",
) : UiState

/** One past match: "City Royale", "Came 2nd · 12 Oct 2026", "+300 coins", "+200 XP". */
data class MatchHistoryRowUi(
    val matchId: String,
    val gameId: String,
    val game: String,
    /** "2nd", or "Abandoned" if the user left; null when unknown. Highlighted in a pill. */
    val place: String?,
    /** The user's place (1 gold, 2 silver, 3 bronze); null when they abandoned or it's unknown. */
    val placeNumber: Int? = null,
    /** The league ("500 coins league", "Free league"), after the place, in its tier's look. */
    val league: String = "",
    /** The league's rank among the leagues by fee (0 = cheapest) and how many there are: its look; -1: plainest. */
    val leagueRank: Int = -1,
    val leagueCount: Int = 0,
    /** A tap opens the match's leaderboard: not for one the user abandoned (left while still playing). */
    val opensBoard: Boolean = true,
    /** The group (or Quick Play) and the date, after the league. */
    val line: String,
    val coins: String,
    /** The coins line is a loss (shown red). */
    val lost: Boolean,
    val xp: String,
)

sealed interface MatchHistoryIntent : UiIntent {
    data object Load : MatchHistoryIntent
    data class Open(val matchId: String) : MatchHistoryIntent
    data object CloseBoard : MatchHistoryIntent
    data object Back : MatchHistoryIntent
}

sealed interface MatchHistoryEffect : UiEffect {
    data object Close : MatchHistoryEffect
}
