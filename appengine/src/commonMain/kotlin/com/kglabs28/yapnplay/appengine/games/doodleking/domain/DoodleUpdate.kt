package com.kglabs28.yapnplay.appengine.games.doodleking.domain

/** One server message: the match and what led to it. */
data class DoodleUpdate(val match: DoodleMatch, val events: List<DoodleEvent>)
