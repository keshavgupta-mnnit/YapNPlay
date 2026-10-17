package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.base.rules.GameRules
import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRules
import com.kglabs28.yapnplay.appengine.games.base.rules.MandatoryRules
import com.kglabs28.yapnplay.appengine.games.base.rules.PlayMode
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.START_BONUS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.STARTING_CASH

/** Mandatory rules — the identity of City Baazar. Never toggled. */
data class CityBaazarMandatoryRules(
    val startingCash: Int = STARTING_CASH,
    val startBonus: Int = START_BONUS,
) : MandatoryRules

/** House rules — a room leader may change these. */
data class CityBaazarHouseRules(
    val setDoubleRent: Boolean = true,
    val restHouseSkips: Boolean = true,
    val clubFee: Boolean = true,
    val roundLimit: Boolean = true,
) : HouseRules

/** Full rules config: mandatory core + toggleable house rules. */
data class CityBaazarRules(
    override val mandatory: CityBaazarMandatoryRules = CityBaazarMandatoryRules(),
    override val house: CityBaazarHouseRules = CityBaazarHouseRules(),
) : GameRules {
    override fun isAllowed(mode: PlayMode): Boolean = when (mode) {
        PlayMode.PRIVATE_ROOM -> true
        PlayMode.PUBLIC_MATCH -> this == STANDARD
    }

    companion object {
        val STANDARD = CityBaazarRules()
    }
}
