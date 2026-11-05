package com.kglabs28.yapnplay.appengine.ui.screens.playinvite

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState

/**
 * App-wide play-now invites (anywhere but a lobby or a table): "Play now?"
 * with the inviter, Join or Not now. Also Nearby play's own messages (the
 * host ended the party), and whether this phone can be found nearby.
 */
data class PlayInviteState(
    val invite: PlayInviteUi? = null,
    /** Answering: the buttons wait. */
    val answering: Boolean = false,
    /** A message card (invite gone, party ended); null when none. */
    val notice: PlayInviteNotice? = null,
) : UiState

/** The invite, ready to draw. */
data class PlayInviteUi(val title: String, val body: String, val avatarId: String)

data class PlayInviteNotice(val title: String, val body: String)

sealed interface PlayInviteIntent : UiIntent {
    data object Load : PlayInviteIntent
    data object Accept : PlayInviteIntent
    data object Decline : PlayInviteIntent
    data object DismissNotice : PlayInviteIntent
    /** The app came to the front ([permitted]: Nearby permission granted) or went to the background. */
    data class Foreground(val inFront: Boolean, val permitted: Boolean) : PlayInviteIntent
    /** The answer to the Nearby permission prompt (asked once when the app is offline). */
    data class NearbyPermission(val granted: Boolean) : PlayInviteIntent
}

sealed interface PlayInviteEffect : UiEffect {
    /** Open the lobby of the party just joined. */
    data class OpenRoom(val roomId: String) : PlayInviteEffect
    /** The Nearby party this phone was in is gone: back to the dashboard. */
    data object PartyEnded : PlayInviteEffect
    /** Offline with no Nearby permission: ask, so phones around can find and invite this one. */
    data object RequestNearbyPermission : PlayInviteEffect
}
