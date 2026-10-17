package com.kglabs28.yapnplay.appengine.games.citybaazar.domain

import kotlinx.datetime.Instant

/** A property up for auction: highest [bid] so far by [leaderSeat] (null: none yet, it opens at [bid]), the [nextBid], and when it closes. */
data class CityBaazarAuctionInfo(
    val tile: Int,
    val sellerSeat: Int,
    val bid: Int,
    val leaderSeat: Int?,
    val nextBid: Int,
    val endsAt: Instant,
)
