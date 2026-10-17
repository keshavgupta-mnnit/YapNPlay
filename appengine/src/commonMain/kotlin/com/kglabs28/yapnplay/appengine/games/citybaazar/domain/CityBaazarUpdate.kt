package com.kglabs28.yapnplay.appengine.games.citybaazar.domain

/** One server message: the new match plus the events that led to it. */
data class CityBaazarUpdate(val match: CityBaazarMatch, val events: List<CityBaazarEvent>)
