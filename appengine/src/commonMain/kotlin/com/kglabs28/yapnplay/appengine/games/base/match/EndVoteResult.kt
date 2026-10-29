package com.kglabs28.yapnplay.appengine.games.base.match

/** How an end vote came out: [ended] the game, with [yes] of [voters] agreeing. */
data class EndVoteResult(val ended: Boolean, val yes: Int, val voters: Int)
