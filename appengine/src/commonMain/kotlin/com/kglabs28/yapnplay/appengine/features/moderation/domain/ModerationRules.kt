package com.kglabs28.yapnplay.appengine.features.moderation.domain

/**
 * Voting rules for reports (the server applies the same): everyone
 * present except the reporter and the reported person votes. With four
 * or fewer voters two agreements are enough, otherwise three are needed;
 * with fewer than two possible voters nobody can confirm, so no report.
 */
object ModerationRules {
    const val MIN_VOTERS = 2
    private const val SMALL_ROOM = 4
    private const val SMALL_ROOM_AGREEMENTS = 2
    private const val AGREEMENTS = 3

    fun canReport(voters: Int): Boolean = voters >= MIN_VOTERS

    fun requiredAgreements(voters: Int): Int =
        if (voters <= SMALL_ROOM) SMALL_ROOM_AGREEMENTS else AGREEMENTS
}
