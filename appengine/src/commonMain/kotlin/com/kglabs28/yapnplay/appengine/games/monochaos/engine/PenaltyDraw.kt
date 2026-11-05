package com.kglabs28.yapnplay.appengine.games.monochaos.engine

import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosState

/** A penalty handed to one seat: the table after it ([state]) and how many cards were really drawn ([taken]). */
internal data class PenaltyDraw(val state: MonoChaosState, val taken: Int)
