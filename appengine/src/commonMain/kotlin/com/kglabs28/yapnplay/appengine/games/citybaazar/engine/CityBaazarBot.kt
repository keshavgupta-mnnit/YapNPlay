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
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.StayInJail
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.BOT_BUILD_RESERVE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.BOT_BUY_RESERVE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.BOT_JAIL_FINE_CASH
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.BOT_SET_BUY_RESERVE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.BidSteps
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoard
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.SET_FOR_DOUBLE

/**
 * City Baazar's computer player. A CPU seat buys and builds while it keeps
 * a reserve (less when the buy makes 3 of a colour), pays its way out of
 * jail when it can spare it, and bids in auctions up to the price (half
 * as much again when it would make 3 of a colour). [cautious] (Autoplay,
 * away, time ran out) only rolls and moves, stays in jail on half rent,
 * and never buys, builds or bids. Both raise debt money by selling what
 * earns the least rent first.
 */
class CityBaazarBot(private val cautious: Boolean) : GameBot {

    override fun chooseAction(state: GameState, seat: SeatNumber): Action? {
        val s = state as CityBaazarState
        val me = seat.value
        if (s.currentSeat != me) return null
        val cash = s.cashOf(me)
        return when (s.phase) {
            CityBaazarPhase.FINISHED, CityBaazarPhase.AUCTION -> null
            CityBaazarPhase.ROLL, CityBaazarPhase.JAIL_ROLL -> RollDice
            CityBaazarPhase.JAIL_CHOICE -> if (!cautious && cash >= BOT_JAIL_FINE_CASH) PayJailFine else StayInJail
            CityBaazarPhase.DEBT -> {
                val debt = s.debt?.total ?: 0
                if (cash + CityBaazarEconomy.liquidationValue(s, me) < debt) {
                    DeclareBankruptcy
                } else {
                    nextSale(s, me)?.let { (tile, building) -> if (building) SellBuilding(tile) else SellTile(tile) } ?: DeclareBankruptcy
                }
            }
            CityBaazarPhase.BUY -> {
                val tile = s.pendingTile ?: return DeclineTile
                val price = CityBaazarBoard.tile(tile).price
                val reserve = if (makesSet(s, me, tile)) BOT_SET_BUY_RESERVE else BOT_BUY_RESERVE
                if (!cautious && cash - price >= reserve) BuyTile else DeclineTile
            }
            CityBaazarPhase.BUILD -> {
                val tile = s.pendingTile ?: return SkipBuild
                if (!cautious && cash - CityBaazarBoard.tile(tile).buildCost >= BOT_BUILD_RESERVE) BuildOnTile(tile) else SkipBuild
            }
        }
    }

    /** Whether [seat] (a CPU bidder) wants to bid the next step in the running auction. */
    fun wantsToBid(s: CityBaazarState, seat: Int): Boolean {
        if (cautious) return false
        val a = s.auction ?: return false
        if (seat == a.seller || seat == a.leader || seat !in s.active) return false
        val next = if (a.leader == null) a.bid else a.bid + BidSteps.after(a.bid)
        val price = CityBaazarBoard.tile(a.tile).price
        val worth = if (makesSet(s, seat, a.tile)) price * SET_WORTH_NUM / SET_WORTH_DEN else price
        return next <= worth && s.cashOf(seat) - next >= BOT_BUY_RESERVE
    }

    private fun makesSet(s: CityBaazarState, seat: Int, tile: Int): Boolean =
        CityBaazarEconomy.colourCount(s, seat, tile) + 1 >= SET_FOR_DOUBLE

    companion object {
        private const val SET_WORTH_NUM = 3
        private const val SET_WORTH_DEN = 2

        /**
         * What to sell next to raise money: (tile, true) for its top
         * building, (tile, false) for the bare tile. Whatever earns the
         * least rent goes first.
         */
        fun nextSale(s: CityBaazarState, seat: Int): Pair<Int, Boolean>? =
            s.owners.filter { it.value == seat }.keys
                .minByOrNull { CityBaazarEconomy.rent(s, it) }
                ?.let { it to (s.level(it) > 0) }
    }
}
