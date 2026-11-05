package com.kglabs28.yapnplay.appengine.features.nearby.host.games

/** What "keep playing" adds at a game's limit: more rounds and/or more minutes (null: not that kind of limit). */
data class ExtensionOffer(
    val extraRounds: Int? = null,
    val extraMinutes: Int? = null,
)
