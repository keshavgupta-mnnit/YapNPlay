package com.kglabs28.yapnplay.appengine.core.ui

/** The match history screen and its rows. */
object HistoryStrings {
    const val historyTitle = "Match History"
    const val historyEmpty = "No matches yet. Play one and it shows up here."
    const val historyFailed = "Could not load your matches. Pull back and try again."
    const val historyBack = "Back"
    fun historyPlace(place: String) = "Came $place"
    const val historyQuickPlay = "Quick Play"
    const val historyRoom = "Room"
    fun historyXp(xp: String) = "+$xp XP"
}
