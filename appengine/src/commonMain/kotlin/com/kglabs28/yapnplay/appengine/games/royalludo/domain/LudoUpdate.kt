package com.kglabs28.yapnplay.appengine.games.royalludo.domain

import com.kglabs28.yapnplay.appengine.games.base.match.EndVoteResult
/** One server message: the new match plus the events that led to it. */
data class LudoUpdate(val match: LudoMatch, val events: List<LudoEvent>,
    /** An end vote that finished in this update (the game ends when [EndVoteResult.ended]). */
    val endVote: EndVoteResult? = null,
)
