package com.kglabs28.yapnplay.appengine.games.mono.domain

/** One server message: the new match plus the events that led to it. */
data class MonoUpdate(val match: MonoMatch, val events: List<MonoEvent>)
