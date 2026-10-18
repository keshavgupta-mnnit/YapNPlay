package com.kglabs28.yapnplay.appengine.core.network

/**
 * The backend no longer has this match (it was restarted, or the match was
 * cleaned up). The table cannot carry on; it tells the player and closes.
 */
class MatchGoneException(val matchId: String) : RuntimeException("match $matchId is gone")
