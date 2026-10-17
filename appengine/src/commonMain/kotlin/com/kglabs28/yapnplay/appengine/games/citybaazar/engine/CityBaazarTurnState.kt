package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.TurnState

data class CityBaazarTurnState(override val seat: SeatNumber, override val phase: String) : TurnState
