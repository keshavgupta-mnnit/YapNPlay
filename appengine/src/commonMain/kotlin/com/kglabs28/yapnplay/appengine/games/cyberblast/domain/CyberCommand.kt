package com.kglabs28.yapnplay.appengine.games.cyberblast.domain

import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir

/** The user's controls and table requests. */
sealed interface CyberCommand {
    /** Pushing [dir] (null: let go); with [bomb], a bomb drops where they stand. */
    data class Input(val dir: BlastDir?, val bomb: Boolean) : CyberCommand
    data class SetMic(val on: Boolean) : CyberCommand
    /** Leaves the match; [giveToUserId] takes the seat (a spectator), null: a CPU or nobody. */
    data class Leave(val giveToUserId: String?) : CyberCommand
    data object ProposeEnd : CyberCommand
    data class VoteEnd(val agree: Boolean) : CyberCommand
}
