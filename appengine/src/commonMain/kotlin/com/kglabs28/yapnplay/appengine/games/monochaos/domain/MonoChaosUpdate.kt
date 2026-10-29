package com.kglabs28.yapnplay.appengine.games.monochaos.domain

import com.kglabs28.yapnplay.appengine.games.base.match.EndVoteResult
/** One server message: the new match plus the events that led to it. */
data class MonoChaosUpdate(val match: MonoChaosMatch, val events: List<MonoChaosEvent>,
    /** An end vote that finished in this update (the game ends when [EndVoteResult.ended]). */
    val endVote: EndVoteResult? = null,
)
