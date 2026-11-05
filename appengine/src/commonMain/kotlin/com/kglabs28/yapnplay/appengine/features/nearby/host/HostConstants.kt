package com.kglabs28.yapnplay.appengine.features.nearby.host

/**
 * Fixed numbers of the phone hosting a Nearby game: the same values the
 * backend uses for its matches, so a table plays the same offline.
 */
object HostConstants {
    const val MILLIS_PER_SECOND = 1_000L
    /** How often the match checks its clocks (turn timers, auctions, votes). */
    const val MATCH_TICK_MS = 1_000L
    /** A bot moves at least this long before its turn's deadline, so the move never races it. */
    const val BOT_DEADLINE_MARGIN_MS = 500L
    /** A guest's dropped connection: this long to come back before the bot plays their seat. */
    const val DISCONNECT_GRACE_MS = 20_000L
    /** Up to this many people at one Nearby table (the host and guests together). */
    const val MAX_PLAYERS = 8
}
