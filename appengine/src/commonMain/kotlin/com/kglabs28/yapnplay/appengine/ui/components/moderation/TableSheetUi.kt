package com.kglabs28.yapnplay.appengine.ui.components.moderation

/** A table's settings sheet and what it leads to: sound and music, reporting someone, a report to vote on, its outcome, the end-game vote. */
sealed interface TableSheetUi {
    /** [canEndGame]: the user may ask everyone to end the game. */
    data class Settings(val effectsOn: Boolean, val musicOn: Boolean, val canEndGame: Boolean = false) : TableSheetUi
    data class Report(val people: List<ReportPersonUi>, val selectedUserId: String?, val warning: String, val error: String?) : TableSheetUi
    data class Vote(val reportId: String, val title: String, val body: String, val secondsLeft: String) : TableSheetUi
    data class Outcome(val title: String, val body: String) : TableSheetUi
    /** Someone asked to end the game: the table votes. */
    data class EndVote(val vote: EndVoteUi) : TableSheetUi
}
