package com.kglabs28.yapnplay.appengine.ui.components.seat

import com.kglabs28.yapnplay.appengine.core.ui.TableStrings

/** The keep-playing vote (every game's) as the user sees it. */
object KeepPlayingUiMapper {
    /** [mySeat]: the user's seat (null: watching); [countdown]: the time left, already worded; [defaultRounds] when the vote doesn't say. */
    fun of(voters: List<Int>, yes: List<Int>, no: List<Int>, extraRounds: Int?, extraMinutes: Int?, mySeat: Int?, countdown: String, defaultRounds: Int = 1): KeepPlayingUi {
        val eligible = mySeat != null && mySeat in voters
        val votedYes = mySeat != null && mySeat in yes
        val votedNo = mySeat != null && mySeat in no
        return KeepPlayingUi(
            body = TableStrings.extendBody(extra(extraRounds, extraMinutes, defaultRounds)),
            tally = TableStrings.extendTally(yes.size, voters.size),
            status = when {
                !eligible -> TableStrings.extendWatching
                votedYes || votedNo -> TableStrings.extendYouVoted(votedYes)
                else -> null
            },
            canVote = eligible && !votedYes && !votedNo,
            countdown = countdown,
        )
    }

    /** "1 round", "30 rounds" or "15 minutes", by which limit was hit. */
    fun extra(rounds: Int?, minutes: Int?, defaultRounds: Int = 1): String = when {
        minutes != null -> TableStrings.extraMinutes(minutes)
        else -> TableStrings.extraRounds(rounds ?: defaultRounds)
    }
}
