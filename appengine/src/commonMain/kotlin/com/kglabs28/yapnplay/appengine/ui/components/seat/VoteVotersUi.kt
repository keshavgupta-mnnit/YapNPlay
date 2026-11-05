package com.kglabs28.yapnplay.appengine.ui.components.seat

/** One person who voted: their name and avatar. */
data class VoterUi(val name: String, val avatarId: String)

/** Who voted for what in a table vote (end the game, keep playing). */
data class VoteVotersUi(val yes: List<VoterUi> = emptyList(), val no: List<VoterUi> = emptyList()) {
    companion object {
        /** The [yes] and [no] seats as voters, looked up with [who] (a seat nobody holds is left out). */
        fun of(yes: List<Int>, no: List<Int>, who: (Int) -> VoterUi?): VoteVotersUi =
            VoteVotersUi(yes.mapNotNull(who), no.mapNotNull(who))
    }
}
