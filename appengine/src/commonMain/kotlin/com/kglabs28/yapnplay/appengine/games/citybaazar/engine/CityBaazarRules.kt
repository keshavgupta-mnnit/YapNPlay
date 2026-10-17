package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.base.rules.GameRules
import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRules
import com.kglabs28.yapnplay.appengine.games.base.rules.MandatoryRules
import com.kglabs28.yapnplay.appengine.games.base.rules.PlayMode
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.ROUND_LIMIT
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.START_BONUS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.STARTING_CASH
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TIME_LIMIT_MINUTES

/** Mandatory rules — the identity of City Baazar. [startingCash] comes from AppManager. */
data class CityBaazarMandatoryRules(
    val startingCash: Int = STARTING_CASH,
    val startBonus: Int = START_BONUS,
) : MandatoryRules

/**
 * House rules — a room leader may change these. [roundLimit] /
 * [timeLimitMinutes] null = off; their values come from AppManager.
 */
data class CityBaazarHouseRules(
    val setDoubleRent: Boolean = true,
    val restHouseSkips: Boolean = true,
    val clubFee: Boolean = true,
    val roundLimit: Int? = ROUND_LIMIT,
    val timeLimitMinutes: Int? = TIME_LIMIT_MINUTES,
) : HouseRules

data class CityBaazarRules(
    override val mandatory: CityBaazarMandatoryRules = CityBaazarMandatoryRules(),
    override val house: CityBaazarHouseRules = CityBaazarHouseRules(),
) : GameRules {
    // Remote values (cash, limits) may change; any toggle set is allowed everywhere for now.
    override fun isAllowed(mode: PlayMode): Boolean = true

    companion object {
        val STANDARD = CityBaazarRules()
    }
}
