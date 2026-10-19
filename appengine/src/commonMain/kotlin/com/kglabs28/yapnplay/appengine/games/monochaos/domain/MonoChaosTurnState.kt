package com.kglabs28.yapnplay.appengine.games.monochaos.domain

import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.TurnState
import kotlinx.serialization.Serializable

@Serializable
data class MonoChaosTurnState(
    override val seat: SeatNumber,
    override val phase: String,
) : TurnState

@Serializable
enum class MonoChaosPhase { AWAITING_PLAY, AWAITING_COLOR_CHOICE, AWAITING_STACK_CHOICE, AWAITING_DRAW_UNTIL_PLAYABLE, FINISHED }
