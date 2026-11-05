package com.kglabs28.yapnplay.appengine.features.nearby.host.match

import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent

/** The keep-playing vote was decided (shared by every game; sent as an ExtensionVoted event). */
data class ExtensionDecided(val continued: Boolean, val yes: Int, val voters: Int) : GameEvent

/** The end-game vote's result (the same for every game; sent as an EndVoted event). */
data class EndVoteDecided(val ended: Boolean, val yes: Int, val voters: Int) : GameEvent
