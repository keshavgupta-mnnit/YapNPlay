package com.kglabs28.yapnplay.appengine.features.seathandover.domain

import kotlinx.datetime.Instant

/**
 * A seat changing hands mid-match, in any game (room matches only): who
 * left it, the current [stage] and when that stage ends (on this device's
 * clock).
 */
data class SeatOffer(
    val seat: Int,
    val leaverName: String,
    val stage: SeatOfferStage,
    val endsAt: Instant,
)
