package com.kglabs28.yapnplay.appengine.games.mono.engine

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
import com.kglabs28.yapnplay.appengine.games.base.play.GameBot
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import com.kglabs28.yapnplay.appengine.games.base.play.GameView
import com.kglabs28.yapnplay.appengine.games.base.play.TypedRejection
import com.kglabs28.yapnplay.appengine.games.base.play.shuffled
import com.kglabs28.yapnplay.appengine.games.base.rules.GameDescriptor
import com.kglabs28.yapnplay.appengine.games.base.rules.GameRules
import com.kglabs28.yapnplay.appengine.games.base.rules.GameShowcase
import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRuleOption
import com.kglabs28.yapnplay.appengine.games.base.rules.MatchmakingConfig
import com.kglabs28.yapnplay.appengine.games.base.rules.TimingConfig
import com.kglabs28.yapnplay.appengine.games.base.rules.TurnPolicy
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoAction
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCard
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoEvent
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoPhase
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoState
import com.kglabs28.yapnplay.appengine.games.mono.domain.isPlayableOn
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoTurnState
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoView
import com.kglabs28.yapnplay.appengine.games.mono.domain.buildMonoDeck
import com.kglabs28.yapnplay.appengine.games.mono.domain.rules.MonoRules
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants

/**
 * Standard MONO: match a card by colour, number or symbol, or play any
 * Prism card. The first seat to empty their hand wins; the rest keep
 * playing for second and third place. Pure: no clock, no network, no
 * hidden randomness — [random] drives every shuffle and the callout
 * window is compared against [now] only where the caller supplies it.
 */
class MonoEngine : Game {
    override val id = GameId(MonoConstants.ID)
    override val displayName = MonoConstants.DISPLAY_NAME
    override val timing = TimingConfig(turnSeconds = MonoConstants.TURN_SECONDS)
    override val matchmaking = MatchmakingConfig(
        seats = MonoConstants.QUICK_PLAY_SEATS,
        searchSeconds = MonoConstants.SEARCH_SECONDS,
    )
    override val showcase = GameShowcase(taglines = MonoConstants.TAGLINES, artKey = MonoConstants.ID)
    override val houseRuleOptions = MonoConstants.HOUSE_RULES
    override val fixedRules = MonoConstants.FIXED_RULES
    override val minPlayers = MonoConstants.PLAYERS_MIN
    override val maxPlayers = MonoConstants.PLAYERS_MAX
    override val maxSpectators: Int? = null
    override val turnPolicy: TurnPolicy = TurnPolicy.Sequential
    override val bot: GameBot = MonoBot()

    override fun defaultRules(): GameRules = MonoRules.TRADITIONAL

    override fun rulesSummary(): String =
        "MONO for 2-8 players: match by colour, number or symbol, or play any Prism card. " +
            "Prism Color Wild picks the next colour; Prism +4 makes the next player draw 4 and lose their turn. " +
            "Call MONO! within 2.5s when down to one card, or draw a penalty. " +
            "15 seconds per turn; first to empty their hand wins."

    override fun start(rules: GameRules, seatCount: Int, random: GameRandom): GameState {
        require(seatCount in MonoConstants.PLAYERS_MIN..MonoConstants.PLAYERS_MAX) {
            "MONO requires ${MonoConstants.PLAYERS_MIN}-${MonoConstants.PLAYERS_MAX} players"
        }
        val monoRules = rules as? MonoRules ?: MonoRules.TRADITIONAL
        val deck = buildMonoDeck().shuffled(random)
        val seats = (1..seatCount).map { SeatNumber(it) }
        val handSize = monoRules.mandatory.startingHandSize

        val hands = mutableMapOf<Int, List<MonoCard>>()
        var dealt = 0
        for (seat in 1..seatCount) {
            hands[seat] = deck.subList(dealt, dealt + handSize)
            dealt += handSize
        }

        // The face card must be a colour card: a Prism would leave the colour
        // of play undecided and nobody could match it.
        val rest = deck.subList(dealt, deck.size).toMutableList()
        var faceIndex = rest.indexOfFirst { !it.isPrism() }
        if (faceIndex < 0) faceIndex = 0
        val face = rest.removeAt(faceIndex)

        val firstSeat = random.nextInt(seatCount) + 1
        return MonoState(
            _gameId = id,
            _rules = monoRules,
            _seats = seats,
            _turn = MonoTurnState(SeatNumber(firstSeat), MonoPhase.AWAITING_PLAY.name),
            hands = hands,
            drawPile = rest,
            discardPile = listOf(face),
            currentSeat = firstSeat,
            phase = MonoPhase.AWAITING_PLAY,
            topCardColor = face.playColor(),
        )
    }

    override fun apply(state: GameState, seat: SeatNumber, action: Action, random: GameRandom): ApplyResult {
        val s = state as MonoState
        if (s.phase == MonoPhase.FINISHED) return rejected("MATCH_ENDED", "Match ended")
        if (seat.value != s.currentSeat) return rejected("NOT_YOUR_TURN", "Not your turn")

        return when (action) {
            is MonoAction.PlayCard -> playCard(s, seat, action.handIndex, random)
            MonoAction.DrawCard -> drawCard(s, seat, random)
            is MonoAction.ChooseColor -> chooseColor(s, seat, action.color)
            MonoAction.CallMono -> callMono(s, seat)
            MonoAction.AcceptStack -> acceptStack(s, seat, random)
            else -> rejected("BAD_REQUEST", "Unknown action for MONO")
        }
    }

    override fun currentActor(state: GameState): SeatNumber = SeatNumber((state as MonoState).currentSeat)

    override fun autoplay(state: GameState, seat: SeatNumber, random: GameRandom): ApplyResult {
        val s = state as MonoState
        if (s.phase == MonoPhase.FINISHED) return rejected("MATCH_ENDED", "Match ended")
        if (seat.value != s.currentSeat) return rejected("NOT_YOUR_TURN", "Not your turn")

        return when (s.phase) {
            MonoPhase.AWAITING_COLOR_CHOICE -> chooseColor(s, seat, MonoConstants.Color.RED)
            MonoPhase.AWAITING_STACK_CHOICE -> acceptStack(s, seat, random)
            else -> {
                val playable = s.handForSeat(seat.value).indexesOf { isPlayable(it, s.discardPile.lastOrNull(), s.topCardColor) }
                if (playable.isNotEmpty()) playCard(s, seat, playable.first(), random) else drawCard(s, seat, random)
            }
        }
    }

    override fun removeSeat(state: GameState, seat: SeatNumber): GameState {
        val s = state as MonoState
        if (seat.value !in s.seats.map { it.value }) return s
        val newSeats = s.seats.filter { it.value != seat.value }
        val newHands = s.hands - seat.value
        // Only one player left: they win by default.
        if (newSeats.size <= 1) {
            val winner = newSeats.firstOrNull()?.value ?: return s.copy(
                seats = newSeats,
                hands = newHands,
                phase = MonoPhase.FINISHED,
                turn = MonoTurnState(SeatNumber(1), MonoPhase.FINISHED.name),
            )
            return s.copy(
                seats = newSeats,
                hands = newHands,
                phase = MonoPhase.FINISHED,
                turn = MonoTurnState(SeatNumber(winner), MonoPhase.FINISHED.name),
            )
        }
        val nextSeat = nextSeat(s, seat.value)
        return s.copy(
            seats = newSeats,
            hands = newHands,
            currentSeat = nextSeat,
            turn = MonoTurnState(SeatNumber(nextSeat), s.phase.name),
        )
    }

    override fun viewFor(state: GameState, viewer: Viewer): GameView {
        val s = state as MonoState
        val ownHand = (viewer as? Viewer.Player)?.let { s.handForSeat(it.seat.value) } ?: emptyList()
        return MonoView(
            seats = s.seats.map { it.value },
            currentSeat = s.currentSeat,
            phase = s.phase.name,
            topCard = s.discardPile.lastOrNull(),
            topCardColor = s.topCardColor,
            hand = ownHand,
            opponentCardCounts = s.seats.associate { it.value to s.cardCountForSeat(it.value) },
            drawPileCount = s.drawPile.size,
            discardPileCount = s.discardPile.size,
            activeStackPenalty = s.activeStackPenalty,
            eliminatedSeats = s.eliminatedSeats,
        )
    }

    override fun isFinished(state: GameState): Boolean = (state as MonoState).phase == MonoPhase.FINISHED

    /** Winner first, then the rest by how few cards they are holding. */
    override fun outcome(state: GameState): MatchOutcome {
        val s = state as MonoState
        val ranking = mutableListOf<RankedSeat>()
        val winner = s.seats.firstOrNull { s.cardCountForSeat(it.value) == 0 }
        if (winner != null) {
            ranking += RankedSeat(winner, SeatStatus.FINISHED, ranking.size + 1)
        }
        val remaining = s.seats
            .filter { it.value != winner?.value }
            .sortedBy { s.cardCountForSeat(it.value) }
        remaining.forEach { ranking += RankedSeat(it, SeatStatus.FINISHED, ranking.size + 1) }
        s.removedSeats.forEach { ranking += RankedSeat(SeatNumber(it), SeatStatus.REMOVED, ranking.size + 1) }
        return MatchOutcome(ranking)
    }

    private fun playCard(s: MonoState, seat: SeatNumber, handIndex: Int, random: GameRandom): ApplyResult {
        val hand = s.handForSeat(seat.value)
        if (handIndex !in hand.indices) return rejected("BAD_CARD", "No card at that position")
        val card = hand[handIndex]
        if (!isPlayable(card, s.discardPile.lastOrNull(), s.topCardColor)) return rejected("NOT_PLAYABLE", "Card does not match")

        val events = mutableListOf<MonoEvent>()
        val newHand = hand.toMutableList()
        newHand.removeAt(handIndex)
        val newHands = s.hands + (seat.value to newHand)
        val newDiscard = s.discardPile + card

        // A Prism card leaves the colour open until its player chooses.
        val goesToColourChoice = card.isPrism()
        // A Prism +4 stacks only when the house rule allows it.
        val setsStack = !card.isPrism() || (s.monoRules.house.stackingAllowed && card is MonoCard.PrismDrawFour)

        var played = s.copy(
            hands = newHands,
            discardPile = newDiscard,
            phase = if (goesToColourChoice) MonoPhase.AWAITING_COLOR_CHOICE else MonoPhase.AWAITING_PLAY,
            topCardColor = if (goesToColourChoice) s.topCardColor else card.playColor(),
            turnCounter = s.turnCounter + 1,
            lastActionSeat = seat.value,
            lastActionCard = card,
            activeStackPenalty = if (setsStack) card.penaltyValue() else s.activeStackPenalty,
            monoCallWindowExpiry = 0,
        )
        events += MonoEvent.CardPlayed(seat, card, played.topCardColor)

        // Down to one card: open the callout window for the caller's server.
        if (newHand.size == 1) {
            played = played.copy(monoCallWindowExpiry = NOW_PLACEHOLDER)
        }

        if (newHand.isEmpty()) {
            return ApplyResult.Success(
                played.copy(
                    phase = MonoPhase.FINISHED,
                    currentSeat = seat.value,
                    turn = MonoTurnState(seat, MonoPhase.FINISHED.name),
                ),
                events + MonoEvent.SeatFinished(seat, 1) + MonoEvent.MatchEnded(listOf(seat.value)),
            )
        }

        // A draw card charges the next player; they stack or take it.
        if (setsStack) {
            val next = nextSeat(s, seat.value)
            val withStack = played.copy(
                currentSeat = next,
                phase = if (s.monoRules.house.stackingAllowed) MonoPhase.AWAITING_STACK_CHOICE else MonoPhase.AWAITING_PLAY,
                turn = MonoTurnState(SeatNumber(next), played.phase.name),
            )
            return ApplyResult.Success(withStack, events)
        }

        val skips = when (card) {
            is MonoCard.ReverseCard -> 2
            is MonoCard.SkipCard -> 2
            is MonoCard.PrismDrawFour -> 2
            else -> 1
        }
        val next = advanceSeats(s, seat.value, skips)
        val afterEffects = when (card) {
            // A 7 swaps hands; a 0 passes every hand on.
            is MonoCard.NumberCard -> when {
                card.number == SEVEN && s.monoRules.house.sevenSwap -> {
                    val partner = nextSeat(s, seat.value)
                    val swapped = newHands + (seat.value to newHands[partner].orEmpty()) + (partner to newHand)
                    played = played.copy(hands = swapped)
                    events += MonoEvent.HandSwapped(seat, SeatNumber(partner))
                    played
                }
                card.number == ZERO && s.monoRules.house.zeroPass -> {
                    events += MonoEvent.HandsPassed("clockwise")
                    played
                }
                else -> played
            }
            else -> played
        }
        return ApplyResult.Success(
            afterEffects.copy(
                currentSeat = next,
                turn = MonoTurnState(SeatNumber(next), afterEffects.phase.name),
            ),
            events,
        )
    }

    private fun drawCard(s: MonoState, seat: SeatNumber, random: GameRandom): ApplyResult {
        // A waiting stack means the player must take it, not draw one.
        if (s.activeStackPenalty > 0) return acceptStack(s, seat, random)
        // One card left in the draw pile: recycle the discard, face aside.
        if (s.drawPile.size <= 1) {
            val face = s.discardPile.lastOrNull()
            val recycled = s.discardPile.dropLast(1).shuffled(random) + s.drawPile
            if (recycled.isEmpty()) {
                return ApplyResult.Success(passTurn(s, seat), listOf(MonoEvent.TurnPassed(seat)))
            }
            val newHand = s.handForSeat(seat.value) + recycled.first()
            return ApplyResult.Success(
                s.copy(
                    hands = s.hands + (seat.value to newHand),
                    drawPile = recycled.drop(1),
                    discardPile = listOfNotNull(face),
                    turnCounter = s.turnCounter + 1,
                    lastActionSeat = seat.value,
                    lastActionCard = recycled.first(),
                ),
                listOf(MonoEvent.CardDrawn(seat, recycled.first())),
            )
        }
        val drawn = s.drawPile.first()
        val newHand = s.handForSeat(seat.value) + drawn
        return ApplyResult.Success(
            s.copy(
                hands = s.hands + (seat.value to newHand),
                drawPile = s.drawPile.drop(1),
                turnCounter = s.turnCounter + 1,
                lastActionSeat = seat.value,
                lastActionCard = drawn,
            ),
            listOf(MonoEvent.CardDrawn(seat, drawn)),
        )
    }

    private fun chooseColor(s: MonoState, seat: SeatNumber, color: MonoConstants.Color): ApplyResult {
        if (s.phase != MonoPhase.AWAITING_COLOR_CHOICE) return rejected("NOT_WAITING", "Not choosing a colour")
        val next = nextSeat(s, seat.value)
        return ApplyResult.Success(
            s.copy(
                topCardColor = color,
                currentSeat = next,
                phase = MonoPhase.AWAITING_PLAY,
                turnCounter = s.turnCounter + 1,
                lastActionSeat = seat.value,
            ),
            listOf(MonoEvent.ColorChosen(seat, color)),
        )
    }

    private fun callMono(s: MonoState, seat: SeatNumber): ApplyResult {
        if (s.handForSeat(seat.value).size != 1) return rejected("NOT_ONE_CARD", "Call MONO! with one card")
        return ApplyResult.Success(
            s.copy(monoCallWindowExpiry = 0, lastActionSeat = seat.value),
            listOf(MonoEvent.MonoCalled(seat)),
        )
    }

    /** Takes the waiting penalty: draws the cards and passes the turn. */
    private fun acceptStack(s: MonoState, seat: SeatNumber, random: GameRandom): ApplyResult {
        val penalty = s.activeStackPenalty
        if (penalty <= 0) return rejected("NO_STACK", "No penalty to accept")
        val drawn = s.drawPile.take(penalty)
        val newHand = s.handForSeat(seat.value) + drawn
        val events = mutableListOf<MonoEvent>()
        drawn.forEach { events += MonoEvent.CardDrawn(seat, it) }
        // A missed MONO! on top of the penalty.
        if (newHand.size == 1 && s.monoCallWindowExpiry > 0) {
            events += MonoEvent.MonoMissed(seat, s.monoRules.mandatory.monoPenaltyCards)
        }
        val next = nextSeat(s, seat.value)
        return ApplyResult.Success(
            s.copy(
                hands = s.hands + (seat.value to newHand),
                drawPile = s.drawPile.drop(drawn.size),
                currentSeat = next,
                phase = MonoPhase.AWAITING_PLAY,
                activeStackPenalty = 0,
                monoCallWindowExpiry = 0,
                turnCounter = s.turnCounter + 1,
                lastActionSeat = seat.value,
                turn = MonoTurnState(SeatNumber(next), MonoPhase.AWAITING_PLAY.name),
            ),
            events + MonoEvent.TurnPassed(seat),
        )
    }

    /** Ends the turn without drawing — used when the draw pile is empty. */
    private fun passTurn(s: MonoState, seat: SeatNumber): MonoState {
        val next = nextSeat(s, seat.value)
        return s.copy(
            currentSeat = next,
            turnCounter = s.turnCounter + 1,
            lastActionSeat = seat.value,
            turn = MonoTurnState(SeatNumber(next), MonoPhase.AWAITING_PLAY.name),
        )
    }

    /** A card matches on its colour, its number/symbol, or because it is a Prism. */
    private fun isPlayable(card: MonoCard, topCard: MonoCard?, topColor: MonoConstants.Color?): Boolean =
        card.isPlayableOn(topCard, topColor)

    private fun nextSeat(s: MonoState, from: Int): Int = advanceSeats(s, from, 1)

    /** [count] seats on from [from], wrapping once around the table. */
    private fun advanceSeats(s: MonoState, from: Int, count: Int): Int {
        val seats = s.seats.map { it.value }
        if (seats.isEmpty()) return from
        val start = seats.indexOf(from).takeIf { it >= 0 } ?: 0
        return seats[(start + count) % seats.size]
    }

    /** The hand indexes matching [predicate], in hand order. */
    private inline fun List<MonoCard>.indexesOf(predicate: (MonoCard) -> Boolean): List<Int> =
        mapIndexedNotNull { index, card -> if (predicate(card)) index else null }

    private fun rejected(code: String, message: String): ApplyResult = ApplyResult.Rejected(TypedRejection(code, message))

    private companion object {
        const val SEVEN = 7
        const val ZERO = 0
        /** Placeholder the server replaces with its own deadline. */
        const val NOW_PLACEHOLDER = 1L
    }
}