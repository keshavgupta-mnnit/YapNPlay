package com.kglabs28.yapnplay.appengine.features.nearby.host.match

/**
 * A player asked to end the match early. Play pauses until it is decided.
 * [voters] are the humans still in, the proposer counting as a yes. It ends
 * the match when more than half say yes; a missing vote counts as no.
 */
class HostEndVote(
    val proposerSeat: Int,
    val voters: Set<Int>,
    val startedAt: Long,
    val endsAt: Long,
) {
    val yes = mutableSetOf(proposerSeat)
    val no = mutableSetOf<Int>()

    val allVoted: Boolean get() = (yes + no).containsAll(voters)
    val ends: Boolean get() = yes.size * 2 > voters.size
    /** No more "yes" votes could make a majority: decide now. */
    val cannotPass: Boolean get() = (voters.size - no.size) * 2 <= voters.size

    /** Records [seat]'s vote; false when they may not vote or already did. */
    fun vote(seat: Int, agree: Boolean): Boolean {
        if (seat !in voters || seat in yes || seat in no) return false
        if (agree) yes += seat else no += seat
        return true
    }
}

/** The keep-playing vote at a game's round or time limit: continue when at least half say yes. */
class HostExtensionVote(
    val voters: Set<Int>,
    val endsAt: Long,
) {
    val yes = mutableSetOf<Int>()
    val no = mutableSetOf<Int>()

    val allVoted: Boolean get() = (yes + no).containsAll(voters)
    val continues: Boolean get() = voters.isNotEmpty() && yes.size * 2 >= voters.size

    /** Records [seat]'s vote; false when they may not vote or already did. */
    fun vote(seat: Int, keepPlaying: Boolean): Boolean {
        if (seat !in voters || seat in yes || seat in no) return false
        if (keepPlaying) yes += seat else no += seat
        return true
    }
}
