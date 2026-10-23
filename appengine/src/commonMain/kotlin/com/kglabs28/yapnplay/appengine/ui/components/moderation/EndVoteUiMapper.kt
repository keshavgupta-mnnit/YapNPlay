package com.kglabs28.yapnplay.appengine.ui.components.moderation

import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.games.base.match.MatchEndVote
import kotlinx.datetime.Instant

/** The end-game vote, for any table, as [EndVoteUi]. Pure. */
object EndVoteUiMapper {
    /**
     * [mySeat] is the user's seat (null: watching), [proposer] who asked,
     * [ranking] how this game ranks players when it ends now.
     */
    fun of(vote: MatchEndVote, mySeat: Int?, proposer: String, ranking: String, now: Instant): EndVoteUi {
        val eligible = mySeat != null && mySeat in vote.voters
        val votedYes = mySeat != null && mySeat in vote.yes
        val votedNo = mySeat != null && mySeat in vote.no
        return EndVoteUi(
            body = TableStrings.endVoteBody(proposer, ranking),
            tally = TableStrings.endVoteTally(vote.yes.size, vote.voters.size),
            status = when {
                !eligible -> TableStrings.endVoteWatching
                votedYes || votedNo -> TableStrings.endVoteYouVoted(votedYes)
                else -> null
            },
            canVote = eligible && !votedYes && !votedNo,
            countdown = TableStrings.seatCountdown((vote.endsAt - now).inWholeSeconds.coerceAtLeast(0)),
        )
    }

    /** Whether the user may ask to end: a person playing for themselves, in a game still on, with no vote running. */
    fun canPropose(isCpu: Boolean, presence: String, finished: Boolean, voteRunning: Boolean): Boolean =
        !isCpu && presence == SeatPresence.PLAYING.name && !finished && !voteRunning

    /** Whether a vote was running in [before] and is gone in [after] without the game ending: it didn't pass. */
    fun rejected(before: MatchEndVote?, after: MatchEndVote?, finished: Boolean): Boolean =
        before != null && after == null && !finished
}
