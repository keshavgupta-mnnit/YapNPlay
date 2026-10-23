package com.kglabs28.yapnplay.appengine.games.monochaos.domain

/** One server message: the new match plus the events that led to it. */
data class MonoChaosUpdate(val match: MonoChaosMatch, val events: List<MonoChaosEvent>)
