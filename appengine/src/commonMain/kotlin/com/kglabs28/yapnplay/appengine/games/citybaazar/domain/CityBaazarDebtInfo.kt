package com.kglabs28.yapnplay.appengine.games.citybaazar.domain

import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.MoneyReason

/** What the current player owes but can't pay yet (they must sell). */
data class CityBaazarDebtInfo(val amount: Int, val reason: MoneyReason, val tile: Int?)
