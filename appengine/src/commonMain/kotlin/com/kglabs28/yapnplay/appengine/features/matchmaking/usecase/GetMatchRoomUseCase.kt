package com.kglabs28.yapnplay.appengine.features.matchmaking.usecase

import com.kglabs28.yapnplay.appengine.core.network.MatchChannel

/** The room a match was played in (null for Quick Play), so closing it can go back to that group. */
class GetMatchRoomUseCase(private val match: MatchChannel) {
    operator fun invoke(matchId: String): String? = match.roomOf(matchId)
}
