package com.kglabs28.yapnplay.appengine.features.matchmaking.domain

/** A match the user still has a seat in (they closed the app or lost connection). */
data class ActiveMatchInfo(
    val matchId: String,
    val gameId: String,
    /** Room name, or "Quick Play". */
    val title: String,
)
