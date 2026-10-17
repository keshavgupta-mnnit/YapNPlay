package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.MoneyReason

/** What the current player owes but can't pay yet (they must sell). */
data class CityBazaarDebtInfo(val amount: Int, val reason: MoneyReason, val tile: Int?)
