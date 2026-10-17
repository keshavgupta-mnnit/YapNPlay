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
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.DeclareBankruptcy
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.DeclineTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.PayJailFine
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SellBuilding
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SellTile
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.SkipBuild
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.action.UseJailPass
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
 * Pure: dice come from the injected [GameRandom].
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
    override val bot = CityBaazarBot(cautious = false)
    /** Plays a turn whose time ran out: rolls, declines buying and building. */
    val timeoutBot = CityBaazarBot(cautious = true)

    override fun defaultRules(): GameRules = CityBaazarRules.STANDARD

    override fun rulesSummary(): String =
        "City Baazar for 2–6 players: ₹25,000 each, ₹1,500 at START, buy cities, " +
            "build on complete colour groups, pay rent, taxes and cards; last one " +
            "solvent wins, or the richest after 30 rounds."

    override fun start(rules: GameRules, seatCount: Int, random: GameRandom): GameState {
        require(seatCount in CITY_BAAZAR_MIN_PLAYERS..CITY_BAAZAR_MAX_PLAYERS) { "City Baazar needs 2-6 players" }
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
        if (seat.value != s.currentSeat) return reject("NOT_YOUR_TURN", "Not your turn")
        val me = seat.value
        val turn = CityBaazarTurn(s, random)
        val ok = when (action) {
            is RollDice -> (s.phase == CityBaazarPhase.ROLL || s.phase == CityBaazarPhase.JAIL_CHOICE).also { if (it) turn.roll() }
            is BuildOnTile -> when (s.phase) {
                CityBaazarPhase.ROLL -> turn.build(me, action.tile)
                CityBaazarPhase.BUILD -> (action.tile == s.pendingTile && turn.build(me, action.tile)).also { if (it) turn.decided() }
                else -> false
            }
            SkipBuild -> (s.phase == CityBaazarPhase.BUILD).also { if (it) turn.decided() }
            BuyTile -> (s.phase == CityBaazarPhase.BUY && turn.buy(me)).also { if (it) turn.decided() }
            DeclineTile -> (s.phase == CityBaazarPhase.BUY).also { if (it) turn.decided() }
            PayJailFine -> s.phase == CityBaazarPhase.JAIL_CHOICE && turn.payJailFine(me)
            UseJailPass -> s.phase == CityBaazarPhase.JAIL_CHOICE && turn.useJailPass(me)
            is SellBuilding -> (s.phase == CityBaazarPhase.DEBT && turn.sellBuilding(me, action.tile)).also { if (it) turn.settleDebtIfPossible() }
            is SellTile -> (s.phase == CityBaazarPhase.DEBT && turn.sellTile(me, action.tile)).also { if (it) turn.settleDebtIfPossible() }
            DeclareBankruptcy -> (s.phase == CityBaazarPhase.DEBT).also { if (it) turn.bankrupt(me, s.debt?.payees.orEmpty()) }
            else -> return reject("BAD_REQUEST", "Unknown action for City Baazar")
        }
        return if (ok) turn.result() else reject("ILLEGAL_ACTION", "Not allowed now")
    }

    override fun currentActor(state: GameState) = SeatNumber((state as CityBaazarState).currentSeat)

    override fun autoplay(state: GameState, seat: SeatNumber, random: GameRandom): ApplyResult = playWith(bot, state, seat, random)

    /** The turn ran out on someone playing for themselves. */
    fun timeoutPlay(state: GameState, seat: SeatNumber, random: GameRandom): ApplyResult = playWith(timeoutBot, state, seat, random)

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
