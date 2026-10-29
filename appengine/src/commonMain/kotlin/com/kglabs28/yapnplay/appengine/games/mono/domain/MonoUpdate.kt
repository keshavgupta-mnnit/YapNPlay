package com.kglabs28.yapnplay.appengine.games.mono.domain

import com.kglabs28.yapnplay.appengine.games.base.match.EndVoteResult
/** One server message: the new match plus the events that led to it. */
data class MonoUpdate(val match: MonoMatch, val events: List<MonoEvent>,
    /** An end vote that finished in this update (the game ends when [EndVoteResult.ended]). */
    val endVote: EndVoteResult? = null,
)
