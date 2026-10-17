package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

/** One server message: the new match plus the events that led to it. */
data class CityBazaarUpdate(val match: CityBazaarMatch, val events: List<CityBazaarEvent>)
