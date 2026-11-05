package com.kglabs28.yapnplay.appengine.ui.screens.playhub

import com.kglabs28.yapnplay.appengine.features.nearby.presentation.NearbyCheck
import com.kglabs28.yapnplay.appengine.features.nearby.presentation.NearbyReadyUi
import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState

/**
 * The Quick Play hub (Games tab): three ways to play — with randoms (the
 * game catalog and matchmaking), with friends (a temporary room) and
 * offline nearby. Everything is formatted here, ready to draw.
 */
data class PlayHubState(
    /** "8 Friends Online"; empty until the friends list loads. */
    val friendsOnlineLabel: String = "",
    /** A few online friends' avatars for the pill. */
    val friendFaces: List<String> = emptyList(),
    /** A party is being set up (online or Nearby): the buttons wait. */
    val creatingParty: Boolean = false,
    /** The backend can be reached: Randoms and Friends need it; Nearby never does. */
    val online: Boolean = true,
    /** A message card (setup failed); null when none. */
    val notice: PlayHubNotice? = null,
    /** "Get ready for Nearby" while something Nearby needs isn't ready; null when closed. */
    val ready: NearbyReadyUi? = null,
) : UiState

/** A message card over the hub. */
data class PlayHubNotice(val title: String, val body: String)

sealed interface PlayHubIntent : UiIntent {
    /** The hub came on screen: fresh online-friends count. */
    data object Load : PlayHubIntent
    data object PlayWithRandoms : PlayHubIntent
    data object PlayWithFriends : PlayHubIntent
    /** Nearby tapped ([permitted]: the permission is granted): host now, or show what isn't ready. */
    data class ScanNearby(val permitted: Boolean) : PlayHubIntent
    /** The answer to the Nearby permission prompt. */
    data class NearbyPermission(val granted: Boolean) : PlayHubIntent
    /** Back in the app (from Settings): the open checklist checks again. */
    data class RecheckReady(val permitted: Boolean) : PlayHubIntent
    /** A row that isn't ready was tapped. */
    data class FixReady(val check: NearbyCheck) : PlayHubIntent
    data object ContinueReady : PlayHubIntent
    data object CloseReady : PlayHubIntent
    data object DismissNotice : PlayHubIntent
}

sealed interface PlayHubEffect : UiEffect {
    /** Show the game catalog (Quick Play with randoms). */
    data object OpenRandoms : PlayHubEffect
    /** The temporary room is ready: open its lobby. */
    data class OpenRoom(val roomId: String) : PlayHubEffect
    /** Ask for the Nearby permission (a checklist row). */
    data object RequestNearbyPermission : PlayHubEffect
    /** The permission was refused for good: the app's settings, to allow it there. */
    data object OpenAppSettings : PlayHubEffect
}
