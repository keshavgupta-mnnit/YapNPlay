package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.base.Game
import com.kglabs28.yapnplay.appengine.games.base.identity.GameId
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.GameState
import com.kglabs28.yapnplay.appengine.games.base.match.MatchOutcome
import com.kglabs28.yapnplay.appengine.games.base.match.RankedSeat
import com.kglabs28.yapnplay.appengine.games.base.match.SeatStatus
import com.kglabs28.yapnplay.appengine.games.base.match.Viewer
import com.kglabs28.yapnplay.appengine.games.base.play.Action
import com.kglabs28.yapnplay.appengine.games.base.play.ApplyResult
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import com.kglabs28.yapnplay.appengine.games.base.play.GameView
import com.kglabs28.yapnplay.appengine.games.base.play.TypedRejection
import com.kglabs28.yapnplay.appengine.games.base.rules.GameRules
import com.kglabs28.yapnplay.appengine.games.base.rules.GameShowcase
import com.kglabs28.yapnplay.appengine.games.base.rules.MatchmakingConfig
import com.kglabs28.yapnplay.appengine.games.base.rules.TimingConfig
import com.kglabs28.yapnplay.appengine.games.base.rules.TurnPolicy
import com.kglabs28.yapnplay.appengine.games.elements.dice.mechanics.RollDice
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.BuildOnTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.BuyTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.CloseAuction
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.DeclareBankruptcy
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.DeclineTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.EndMatch
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.LateRollFine
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.PayJailFine
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.PlaceBid
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SellBuilding
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SellTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SkipBuild
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.StartAuction
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.StayInJail
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_DISPLAY_NAME
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_FIXED_RULES
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_HOUSE_RULES
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_ID
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_MAX_PLAYERS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_MIN_PLAYERS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_QUICK_PLAY_SEATS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_SEARCH_SECONDS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_TAGLINES
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_TURN_SECONDS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.START_TILE

/**
 * City Baazar: the Indian city-trading board game (docs/city-baazar-rules.md).
 * Pure: dice come from the injected [GameRandom]. Bids come from any player;
 * the server applies [CloseAuction], [EndMatch] and [LateRollFine].
 */
class CityBaazarEngine : Game {
    override val id = GameId(CITY_BAAZAR_ID)
    override val displayName = CITY_BAAZAR_DISPLAY_NAME
    override val timing = TimingConfig(turnSeconds = CITY_BAAZAR_TURN_SECONDS)
    override val matchmaking = MatchmakingConfig(seats = CITY_BAAZAR_QUICK_PLAY_SEATS, searchSeconds = CITY_BAAZAR_SEARCH_SECONDS)
    override val showcase = GameShowcase(taglines = CITY_BAAZAR_TAGLINES, artKey = CITY_BAAZAR_ID)
    override val houseRuleOptions = CITY_BAAZAR_HOUSE_RULES
    override val fixedRules = CITY_BAAZAR_FIXED_RULES
    override val minPlayers = CITY_BAAZAR_MIN_PLAYERS
    override val maxPlayers = CITY_BAAZAR_MAX_PLAYERS
    override val maxSpectators: Int? = null
    override val turnPolicy: TurnPolicy = TurnPolicy.Sequential
    /** CPU seats: buys, builds and bids. */
    override val bot = CityBaazarBot(cautious = false)
    /** Autoplay, away and timed-out turns: rolls and moves, sells only to pay. */
    val cautiousBot = CityBaazarBot(cautious = true)

    override fun defaultRules(): GameRules = CityBaazarRules.STANDARD

    override fun rulesSummary(): String =
        "City Baazar for 3–6 players (random tables 4): buy cities, build on your own, " +
            "double rent with 3 of a colour, ₹1,500 at START; sell or auction to pay debts; " +
            "last one solvent wins, or the richest when time or rounds run out."

    override fun start(rules: GameRules, seatCount: Int, random: GameRandom): GameState {
        require(seatCount in 2..CITY_BAAZAR_MAX_PLAYERS) { "City Baazar needs 2-6 seats" }
        val cbRules = rules as? CityBaazarRules ?: CityBaazarRules.STANDARD
        val seats = (1..seatCount).toList()
        return CityBaazarState(
            gameId = id,
            cbRules = cbRules,
            seats = seats.map { SeatNumber(it) },
            seatCount = seatCount,
            positions = seats.associateWith { START_TILE },
            cash = seats.associateWith { cbRules.mandatory.startingCash },
            currentSeat = 1,
            phase = CityBaazarPhase.ROLL,
        )
    }

    override fun apply(state: GameState, seat: SeatNumber, action: Action, random: GameRandom): ApplyResult {
        val s = state as CityBaazarState
        if (s.phase == CityBaazarPhase.FINISHED) return reject("MATCH_ENDED", "Match ended")
        val me = seat.value
        val turn = CityBaazarTurn(s, random)
        // Any player may bid; everything else is the current seat's (or the server's on their behalf).
        if (action is PlaceBid) return if (s.phase == CityBaazarPhase.AUCTION && turn.bid(me)) turn.result() else reject("ILLEGAL_ACTION", "Can't bid now")
        if (me != s.currentSeat) return reject("NOT_YOUR_TURN", "Not your turn")
        val ok = when (action) {
            is RollDice -> when (s.phase) {
                CityBaazarPhase.ROLL -> true.also { turn.roll() }
                CityBaazarPhase.JAIL_ROLL -> true.also { turn.jailRoll() }
                else -> false
            }
            BuyTile -> (s.phase == CityBaazarPhase.BUY && turn.buy(me)).also { if (it) turn.decided() }
            DeclineTile -> (s.phase == CityBaazarPhase.BUY).also { if (it) turn.decided() }
            is BuildOnTile -> (s.phase == CityBaazarPhase.BUILD && action.tile == s.pendingTile && turn.build(me, action.tile)).also { if (it) turn.decided() }
            SkipBuild -> (s.phase == CityBaazarPhase.BUILD).also { if (it) turn.decided() }
            PayJailFine -> s.phase == CityBaazarPhase.JAIL_CHOICE && turn.payJailFine(me)
            StayInJail -> (s.phase == CityBaazarPhase.JAIL_CHOICE).also { if (it) turn.stayInJail(me) }
            is SellBuilding -> (s.phase == CityBaazarPhase.DEBT && turn.sellBuilding(me, action.tile)).also { if (it) turn.settleDebtIfPossible() }
            is SellTile -> (s.phase == CityBaazarPhase.DEBT && turn.sellTile(me, action.tile)).also { if (it) turn.settleDebtIfPossible() }
            is StartAuction -> s.phase == CityBaazarPhase.DEBT && turn.startAuction(me, action.tile)
            CloseAuction -> s.phase == CityBaazarPhase.AUCTION && turn.closeAuction()
            DeclareBankruptcy -> (s.phase == CityBaazarPhase.DEBT).also { if (it) turn.bankrupt(me, s.debt?.payees.orEmpty()) }
            LateRollFine -> (s.phase == CityBaazarPhase.ROLL || s.phase == CityBaazarPhase.JAIL_ROLL).also { if (it) turn.lateRollFine(me) }
            EndMatch -> true.also { turn.finish() }
            else -> return reject("BAD_REQUEST", "Unknown action for City Baazar")
        }
        return if (ok) turn.result() else reject("ILLEGAL_ACTION", "Not allowed now")
    }

    override fun currentActor(state: GameState) = SeatNumber((state as CityBaazarState).currentSeat)

    /** A CPU seat's move. */
    override fun autoplay(state: GameState, seat: SeatNumber, random: GameRandom): ApplyResult = playWith(bot, state, seat, random)

    /**
     * Autoplay / away / time ran out: roll and move, decline buying and
     * building, stay in jail on half rent, sell only what a debt needs.
     * [lateFine]: the user didn't roll and Autoplay is off (₹50 first).
     */
    fun cautiousPlay(state: GameState, seat: SeatNumber, random: GameRandom, lateFine: Boolean): ApplyResult {
        val s = state as CityBaazarState
        if (!lateFine || (s.phase != CityBaazarPhase.ROLL && s.phase != CityBaazarPhase.JAIL_ROLL)) return playWith(cautiousBot, s, seat, random)
        val fined = apply(s, seat, LateRollFine, random) as? ApplyResult.Success ?: return playWith(cautiousBot, s, seat, random)
        return when (val then = playWith(cautiousBot, fined.state, seat, random)) {
            is ApplyResult.Success -> ApplyResult.Success(then.state, fined.events + then.events)
            is ApplyResult.Rejected -> fined
        }
    }

    private fun playWith(player: CityBaazarBot, state: GameState, seat: SeatNumber, random: GameRandom): ApplyResult {
        val action = player.chooseAction(state, seat) ?: return reject("NO_ACTION", "Nothing to do")
        return apply(state, seat, action, random)
    }

    override fun removeSeat(state: GameState, seat: SeatNumber): GameState {
        val s = state as CityBaazarState
        if (seat.value !in s.active) return s
        val turn = CityBaazarTurn(s, NO_RANDOM)
        turn.remove(seat.value)
        return turn.state()
    }

    override fun viewFor(state: GameState, viewer: Viewer): GameView = CityBaazarView(state as CityBaazarState)

    override fun isFinished(state: GameState): Boolean = (state as CityBaazarState).phase == CityBaazarPhase.FINISHED

    override fun outcome(state: GameState): MatchOutcome {
        val s = state as CityBaazarState
        return MatchOutcome(
            ranking(s).mapIndexed { index, seat ->
                RankedSeat(SeatNumber(seat), if (seat in s.active) SeatStatus.FINISHED else SeatStatus.REMOVED, index + 1)
            },
        )
    }

    private fun reject(code: String, message: String) = ApplyResult.Rejected(TypedRejection(code, message))

    companion object {
        /** Players still in by net worth, then bankrupt players (latest first). */
        fun ranking(s: CityBaazarState): List<Int> =
            s.active.sortedByDescending { CityBaazarEconomy.netWorth(s, it) } + s.bankruptOrder.reversed()

        /** Removing a seat never rolls. */
        private val NO_RANDOM = object : GameRandom {
            override fun nextInt(bound: Int): Int = 0
        }
    }
}
