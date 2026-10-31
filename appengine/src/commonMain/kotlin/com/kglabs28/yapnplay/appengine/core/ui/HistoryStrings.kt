package com.kglabs28.yapnplay.appengine.core.ui

/** The match history screen and its rows. */
object HistoryStrings {
    const val historyTitle = "Match History"
    const val historyEmpty = "No matches yet. Play one and it shows up here."
    const val historyFailed = "Could not load your matches. Pull back and try again."
    const val historyBack = "Back"
    const val historyAbandoned = "Abandoned"
    const val historyQuickPlay = "Quick Play"
    const val historyRoom = "Room"
    fun historyLeague(entry: String) = "$entry coins league"
    const val historyFreeLeague = "Free league"
    fun historyXp(xp: String) = "+$xp XP"
    /** The match's net for the user: what they won and got for finishing, less the entry. */
    fun historyNetGain(coins: String) = "Net +$coins coins"
    fun historyNetLoss(coins: String) = "Net -$coins coins"
    const val historyNetEven = "Net 0 coins"
}
