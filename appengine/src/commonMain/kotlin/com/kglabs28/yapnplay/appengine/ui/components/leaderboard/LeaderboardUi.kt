package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

/** The end-of-match leaderboard: which game, how long it took, everyone in finishing order, the user's place and EXP. */
data class LeaderboardUi(
    val game: String,
    val duration: String,
    val entries: List<LeaderboardEntryUi>,
    /** For Share: e.g. "2nd". */
    val myPlace: String,
    /** "+300". */
    val myExp: String = "",
    /** "+100 coins": the user's own finishing bonus; empty when they got none. */
    val myBonus: String = "",
    /** "500 COINS LEAGUE" / "FREE LEAGUE", so the coins below make sense; empty until the result is in. */
    val league: String = "",
)
