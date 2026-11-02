package com.kglabs28.yapnplay.appengine.ui.screens.main

import com.kglabs28.yapnplay.appengine.ui.components.moderation.AppSettingsUi
import com.kglabs28.yapnplay.appengine.ui.components.coins.CoinsOfferUi
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
    /** The user's preset avatar (the top bar shows it; follows profile edits). */
    val avatarId: String = "",
    val level: String = "",
    val online: Boolean = false,
    /** False until the first profile load lands; the top bar keeps its
     *  layout and only fades the values in, so nothing jumps. */
    val profileLoaded: Boolean = false,
    val coinBalance: String = "",
    /** 0–1 towards the next level, and "350 / 1,000 XP". */
    val levelProgress: Float = 0f,
    val xpLabel: String = "",
    /** "+100": the daily bonus the server just added, until the user collects it; null otherwise. */
    val dailyBonus: String? = null,
    val selectedTab: AppNavTab = AppNavTab.HOME,
    /** Bell history, newest first (at most 10). */
    val notifications: List<NotificationUi> = emptyList(),
    val unreadNotifications: Int = 0,
    val notificationsOpen: Boolean = false,
    /** A match the user still has a seat in: "Rejoin?" dialog; null when none. */
    val rejoin: RejoinUi? = null,
    /** How a friend link's request went ("Request sent to K7M2PX", "Already friends"); null when none is showing. */
    val friendLink: FriendLinkUi? = null,
    /** The coins "+": watch an ad for free coins; null when closed. */
    val coinsOffer: CoinsOfferUi? = null,
    /** The settings dialog (sound, music, match history); null when closed. */
    val settings: AppSettingsUi? = null,
    /** Back was pressed here: "Exit YapNPlay?" is showing. */
    val exitAsked: Boolean = false,
) : UiState

/** The "your game is still on" dialog. */
data class RejoinUi(val matchId: String, val gameId: String, val title: String)

/** The friend link's outcome; [failed] shows it as a problem. */
data class FriendLinkUi(val message: String, val failed: Boolean)

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
    data object CollectDailyBonus : MainIntent
    data class SelectTab(val tab: AppNavTab) : MainIntent
    data object LoadProfile : MainIntent
    data object OpenSettings : MainIntent
    /** Bell tapped: opens or closes the drop-down. */
    data object ToggleNotifications : MainIntent
    /** Drop-down closed by tapping outside or back. */
    data object DismissNotifications : MainIntent
    data class NotificationClicked(val roomId: String) : MainIntent
    data object Rejoin : MainIntent
    /** The cross: hide the card, the seat stays the user's. */
    data object DismissRejoin : MainIntent
    /** "Don't want it": leave the match for good (a CPU plays on), as from the table. */
    data object DeclineRejoin : MainIntent
    /** A friend link was opened: send its owner a friend request. */
    data class AddFriendFromLink(val userCode: String) : MainIntent
    data object DismissFriendLink : MainIntent
    /** System Back on the main screen: ask before closing the app. */
    data object AskExit : MainIntent
    data object DismissExit : MainIntent
    /** The coins "+" in the top bar. */
    data object OpenGetCoins : MainIntent
    data object WatchAdForCoins : MainIntent
    data object CloseGetCoins : MainIntent
    data object CloseSettings : MainIntent
    data object ToggleEffects : MainIntent
    data object ToggleMusic : MainIntent
    /** The phone's keyboard instead of the app's. */
    data object ToggleKeyboard : MainIntent
    /** Match History in the settings. */
    data object OpenHistory : MainIntent
    /** Feedback & support in the settings: the form ([compose]) or the sent messages. */
    data class OpenFeedback(val compose: Boolean) : MainIntent
}

sealed interface MainEffect : UiEffect {
    data object OpenHistory : MainEffect
    data class OpenFeedback(val compose: Boolean) : MainEffect
    data class OpenRoom(val roomId: String) : MainEffect
    data class OpenMatch(val gameId: String, val matchId: String) : MainEffect
}
