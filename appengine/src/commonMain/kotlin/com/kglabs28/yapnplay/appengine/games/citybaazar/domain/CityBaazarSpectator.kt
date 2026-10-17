package com.kglabs28.yapnplay.appengine.games.citybaazar.domain

/** Someone watching a room match (top bar only). */
data class CityBaazarSpectator(val userId: String, val name: String, val micOn: Boolean)
