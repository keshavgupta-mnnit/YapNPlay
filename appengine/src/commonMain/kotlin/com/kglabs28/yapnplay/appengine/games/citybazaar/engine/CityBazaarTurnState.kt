package com.kglabs28.yapnplay.appengine.games.citybazaar.engine

import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.TurnState

data class CityBazaarTurnState(override val seat: SeatNumber, override val phase: String) : TurnState
