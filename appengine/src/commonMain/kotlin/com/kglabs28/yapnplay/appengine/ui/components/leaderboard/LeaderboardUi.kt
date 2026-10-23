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
    /** "+350": the user's coins (pot + finishing); empty until paid. */
    val myCoins: String = "",
)
