package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

/** Someone watching a room match (top bar only). */
data class CityBazaarSpectator(val userId: String, val name: String, val micOn: Boolean)
