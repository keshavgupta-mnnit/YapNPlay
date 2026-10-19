package com.kglabs28.yapnplay.appengine.games.monochaos.domain

import com.kglabs28.yapnplay.appengine.games.base.play.GameView
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import kotlinx.serialization.Serializable

/**
 * What one viewer sees of a MONO: Complete Chaos table. A spectator gets
 * the public table only; a player also gets their own [hand]. Never send
 * another player's cards and hide them in the app.
 */
@Serializable
data class MonoChaosView(
    val seats: List<Int>,
    val currentSeat: Int,
    val phase: String,
    val topCard: MonoChaosCard?,
    val topCardColor: MonoChaosConstants.Color?,
    val hand: List<MonoChaosCard>,
    val opponentCardCounts: Map<Int, Int>,
    val drawPileCount: Int,
    val discardPileCount: Int,
    /** Deadline for the MONO! callout; 0 when the window is shut. */
    val monoCallWindowExpiry: Long = 0,
    /** Penalty waiting to be stacked onto or taken; 0 when none. */
    val activeStackPenalty: Int = 0,
    val eliminatedSeats: Set<Int> = emptySet(),
) : GameView