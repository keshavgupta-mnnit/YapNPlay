package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.GameState
import com.kglabs28.yapnplay.appengine.games.base.play.Action
import com.kglabs28.yapnplay.appengine.games.base.play.GameBot
import com.kglabs28.yapnplay.appengine.games.elements.dice.mechanics.RollDice
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.BuildOnTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.BuyTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.DeclareBankruptcy
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.DeclineTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.PayJailFine
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SellBuilding
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SellTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SkipBuild
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.UseJailPass
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.BOT_BUILD_RESERVE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.BOT_BUY_RESERVE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.BOT_JAIL_FINE_CASH
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.BOT_SET_BUY_RESERVE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoard

/**
 * City Baazar's computer player. Builds before rolling while it keeps a
 * reserve (biggest rent jump first), buys when it keeps a reserve (less when
 * the buy completes a group), leaves jail by pass or fine when it can spare
 * it, and raises debt money by selling the cheapest buildings, then tiles
 * outside complete groups. [cautious] plays a timed-out turn: roll, never
 * buy or build, sell only what a debt needs.
 */
class CityBaazarBot(private val cautious: Boolean) : GameBot {

    override fun chooseAction(state: GameState, seat: SeatNumber): Action? {
        val s = state as CityBaazarState
        val me = seat.value
        if (s.currentSeat != me) return null
        val cash = s.cashOf(me)
        return when (s.phase) {
            CityBaazarPhase.FINISHED -> null
            CityBaazarPhase.DEBT -> {
                val debt = s.debt?.total ?: 0
                if (cash + CityBaazarEconomy.liquidationValue(s, me) < debt) {
                    DeclareBankruptcy
                } else {
                    nextSale(s, me)?.let { (tile, building) -> if (building) SellBuilding(tile) else SellTile(tile) } ?: DeclareBankruptcy
                }
            }
            CityBaazarPhase.JAIL_CHOICE -> when {
                cautious -> RollDice
                (s.jailPasses[me] ?: 0) > 0 -> UseJailPass
                cash >= BOT_JAIL_FINE_CASH -> PayJailFine
                else -> RollDice
            }
            CityBaazarPhase.BUY -> {
                val tile = s.pendingTile ?: return DeclineTile
                val price = CityBaazarBoard.tile(tile).price
                val reserve = if (completesGroup(s, me, tile)) BOT_SET_BUY_RESERVE else BOT_BUY_RESERVE
                if (!cautious && cash - price >= reserve) BuyTile else DeclineTile
            }
            CityBaazarPhase.BUILD -> {
                val tile = s.pendingTile ?: return SkipBuild
                if (!cautious && cash - CityBaazarBoard.tile(tile).buildCost >= BOT_BUILD_RESERVE) BuildOnTile(tile) else SkipBuild
            }
            CityBaazarPhase.ROLL -> if (cautious) RollDice else bestBuild(s, me)?.let { BuildOnTile(it) } ?: RollDice
        }
    }

    /** The build that raises rent the most while keeping the reserve. */
    private fun bestBuild(s: CityBaazarState, me: Int): Int? =
        CityBaazarEconomy.buildable(s, me)
            .filter { s.cashOf(me) - CityBaazarBoard.tile(it).buildCost >= BOT_BUILD_RESERVE }
            .maxByOrNull { CityBaazarEconomy.cityRent(it, s.level(it) + 1) - CityBaazarEconomy.cityRent(it, s.level(it)) }

    private fun completesGroup(s: CityBaazarState, me: Int, tile: Int): Boolean {
        val group = CityBaazarBoard.tile(tile).group ?: return false
        return CityBaazarBoard.groupTiles(group).all { it == tile || s.owners[it] == me }
    }

    companion object {
        /**
         * What to sell next to raise money: (tile, true) for a building,
         * (tile, false) for the tile itself. Cheapest buildings first, then
         * tiles outside complete groups, cheapest first.
         */
        fun nextSale(s: CityBaazarState, seat: Int): Pair<Int, Boolean>? {
            val mine = s.owners.filter { it.value == seat }.keys
            mine.filter { s.level(it) > 0 }.minByOrNull { CityBaazarBoard.tile(it).buildCost }?.let { return it to true }
            return mine.sortedWith(compareBy({ CityBaazarEconomy.ownsGroup(s, seat, it) }, { CityBaazarBoard.tile(it).price }))
                .firstOrNull()?.let { it to false }
        }
    }
}
