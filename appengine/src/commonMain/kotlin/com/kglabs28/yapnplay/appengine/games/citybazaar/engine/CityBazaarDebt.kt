package com.kglabs28.yapnplay.appengine.games.citybazaar.engine

import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.MoneyReason

/**
 * Money the current seat owes but can't pay yet: [total], of which
 * [payees] (seat → amount) go to players and the rest to the bank.
 */
data class CityBazaarDebt(val total: Int, val payees: Map<Int, Int>, val reason: MoneyReason, val tile: Int?)
