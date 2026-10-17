package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

/** [seller]'s [tile] is up for auction: [bid] is the highest so far by [leader] (null: nobody yet; it opens at [bid]). */
data class CityBaazarAuction(val tile: Int, val seller: Int, val bid: Int, val leader: Int?)
