package com.kglabs28.yapnplay.appengine.games.monochaos.engine

import com.kglabs28.yapnplay.appengine.games.base.Game
import com.kglabs28.yapnplay.appengine.games.base.identity.GameId
import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.match.*
import com.kglabs28.yapnplay.appengine.games.base.play.*
import com.kglabs28.yapnplay.appengine.games.base.rules.*
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.*
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.rules.MonoChaosRules
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants.Color

/**
 * MONO: Complete Chaos: match on colour, number or symbol; stack draw
 * cards (+2, +4, +6, +10) onto each other; a 7 swaps hands and a 0
 * passes every hand; 25 cards in hand is a knockout; drawing runs until
 * something is playable. Pure: [random] drives every shuffle.
 */
class MonoChaosEngine : Game {
    override val id = GameId(MonoChaosConstants.ID)
    override val displayName = MonoChaosConstants.DISPLAY_NAME
    override val timing = TimingConfig(turnSeconds = MonoChaosConstants.TURN_SECONDS)
    override val matchmaking = MatchmakingConfig(
        seats = MonoChaosConstants.QUICK_PLAY_SEATS,
        searchSeconds = MonoChaosConstants.SEARCH_SECONDS,
    )
    override val showcase = GameShowcase(taglines = MonoChaosConstants.TAGLINES, artKey = MonoChaosConstants.ID)
    override val houseRuleOptions = MonoChaosConstants.HOUSE_RULES
    override val fixedRules = MonoChaosConstants.FIXED_RULES
    override val minPlayers = MonoChaosConstants.PLAYERS_MIN
    override val maxPlayers = MonoChaosConstants.PLAYERS_MAX
    override val maxSpectators: Int? = null
    override val turnPolicy: TurnPolicy = TurnPolicy.Sequential
    override val bot = MonoChaosBot()

    override fun defaultRules(): GameRules = MonoChaosRules.TRADITIONAL

    override fun rulesSummary(): String =
        "MONO: Complete Chaos for 2-8 players: match by color, number, or symbol. " +
            "Rule of 7 swaps hands; Rule of 0 passes all hands. " +
            "Stack draw cards (+2, +4, +6, +10); 25+ cards knocks you out. " +
            "Draw until playable. Prism Color Roulette picks a random victim color. " +
            "15 seconds per turn; win by emptying hand or being last standing."

    override fun start(rules: GameRules, seatCount: Int, random: GameRandom): GameState {
        require(seatCount in MonoChaosConstants.PLAYERS_MIN..MonoChaosConstants.PLAYERS_MAX) {
            "MONO: Complete Chaos requires 2-8 players"
        }
        val chaosRules = rules as? MonoChaosRules ?: MonoChaosRules.TRADITIONAL
        val deck = buildMonoChaosDeck().shuffled(random)
        val seats = (1..seatCount).map { SeatNumber(it) }
        val handSize = chaosRules.mandatory.startingHandSize

        val hands = mutableMapOf<Int, List<MonoChaosCard>>()
        var cardIndex = 0
        for (seat in 1..seatCount) {
            hands[seat] = deck.subList(cardIndex, cardIndex + handSize)
            cardIndex += handSize
        }

        val drawPile = deck.subList(cardIndex, deck.size).toMutableList()

        var discardPile = mutableListOf<MonoChaosCard>()
        var topCardColor: Color? = null
        var firstCard = drawPile.removeAt(0)
        while (firstCard.isPrism()) {
            drawPile.add(firstCard)
            val reshuffled = drawPile.shuffled(random)
            drawPile.clear()
            drawPile.addAll(reshuffled)
            firstCard = drawPile.removeAt(0)
        }
        discardPile.add(firstCard)
        topCardColor = firstCard.playColor()

        val firstSeat = random.nextInt(seatCount) + 1
        return MonoChaosState(
            _gameId = id, _rules = chaosRules, _seats = seats,
            _turn = MonoChaosTurnState(SeatNumber(firstSeat), MonoChaosPhase.AWAITING_PLAY.name),
            hands = hands, drawPile = drawPile, discardPile = discardPile.toList(),
            currentSeat = firstSeat, phase = MonoChaosPhase.AWAITING_PLAY,
            topCardColor = topCardColor,
        )
    }

    override fun apply(state: GameState, seat: SeatNumber, action: Action, random: GameRandom): ApplyResult {
        val s = state as MonoChaosState
        if (s.phase == MonoChaosPhase.FINISHED) return ApplyResult.Rejected(TypedRejection("MATCH_ENDED", "Match ended"))
        if (s.isEliminated(seat.value)) return ApplyResult.Rejected(TypedRejection("SEAT_ELIMINATED", "Seat eliminated"))
        if (seat.value != s.currentSeat) return ApplyResult.Rejected(TypedRejection("NOT_YOUR_TURN", "Not your turn"))

        return when (action) {
            is MonoChaosAction.PlayCard -> handlePlayCard(s, seat, action.handIndex, random)
            is MonoChaosAction.DrawUntilPlayable -> handleDrawUntilPlayable(s, seat, random)
            is MonoChaosAction.ChooseColor -> handleChooseColor(s, seat, action.color, random)
            is MonoChaosAction.ChooseRouletteColor -> handleChooseRouletteColor(s, seat, action.color, random)
            is MonoChaosAction.CallMono -> handleCallMono(s, seat, random)
            is MonoChaosAction.StackDraw -> handleStackDraw(s, seat, action.handIndex, random)
            is MonoChaosAction.AcceptStack -> handleAcceptStack(s, seat, random)
            else -> ApplyResult.Rejected(TypedRejection("BAD_REQUEST", "Unknown action for MONO: Complete Chaos"))
        }
    }

    override fun currentActor(state: GameState) = SeatNumber((state as MonoChaosState).currentSeat)

    override fun autoplay(state: GameState, seat: SeatNumber, random: GameRandom): ApplyResult {
        val s = state as MonoChaosState
        if (s.phase == MonoChaosPhase.FINISHED) return ApplyResult.Rejected(TypedRejection("MATCH_ENDED", "Match ended"))
        if (s.isEliminated(seat.value)) return ApplyResult.Rejected(TypedRejection("SEAT_ELIMINATED", "Seat eliminated"))
        if (seat.value != s.currentSeat) return ApplyResult.Rejected(TypedRejection("NOT_YOUR_TURN", "Not your turn"))

        return when (s.phase) {
            MonoChaosPhase.AWAITING_PLAY -> {
                if (s.activeStackPenalty > 0) {
                    val stackable = s.handForSeat(seat.value).withIndex()
                        .filter { (_, card) -> card.penaltyValue() >= s.activeStackPenalty && card.penaltyValue() > 0 }
                    if (stackable.isNotEmpty()) {
                        return handleStackDraw(s, seat, stackable.first().index, random)
                    }
                    return handleAcceptStack(s, seat, random)
                }
                val legalCards = s.handForSeat(seat.value).withIndex()
                    .filter { (_, card) -> isPlayable(card, s.discardPile.lastOrNull(), s.topCardColor) }
                if (legalCards.isNotEmpty()) {
                    return handlePlayCard(s, seat, legalCards.first().index, random)
                } else {
                    return handleDrawUntilPlayable(s, seat, random)
                }
            }
            MonoChaosPhase.AWAITING_COLOR_CHOICE -> {
                return handleChooseColor(s, seat, Color.RED, random)
            }
            MonoChaosPhase.AWAITING_STACK_CHOICE -> {
                // A penalty is on the table: stack onto it if the bot can,
                // otherwise take it. Rejecting here would stall every
                // autoplay seat the moment a draw card landed.
                val stackable = s.handForSeat(seat.value).withIndex()
                    .filter { (_, card) -> card.penaltyValue() >= s.activeStackPenalty && card.penaltyValue() > 0 }
                return if (stackable.isNotEmpty()) {
                    handleStackDraw(s, seat, stackable.first().index, random)
                } else {
                    handleAcceptStack(s, seat, random)
                }
            }
            MonoChaosPhase.AWAITING_DRAW_UNTIL_PLAYABLE -> {
                return ApplyResult.Rejected(TypedRejection("BAD_REQUEST", "Not waiting for draw until playable"))
            }
            MonoChaosPhase.FINISHED -> ApplyResult.Rejected(TypedRejection("MATCH_ENDED", "Match ended"))
        }
    }

    override fun removeSeat(state: GameState, seat: SeatNumber): GameState {
        val s = state as MonoChaosState
        if (seat.value !in s.seats.map { it.value }) return s
        val newSeats = s.seats.filter { it.value != seat.value }
        val newHands = s.hands - seat.value
        var newCurrentSeat = s.currentSeat
        if (seat.value == s.currentSeat) {
            newSeats.firstOrNull()?.let { newCurrentSeat = it.value } ?: return s.copy(
                seats = newSeats, phase = MonoChaosPhase.FINISHED,
                turn = MonoChaosTurnState(SeatNumber(1), MonoChaosPhase.FINISHED.name),
            )
        }
        return s.copy(
            seats = newSeats, hands = newHands, currentSeat = newCurrentSeat,
            phase = if (newSeats.size <= 1) MonoChaosPhase.FINISHED else s.phase,
            turn = MonoChaosTurnState(SeatNumber(newCurrentSeat), s.phase.name),
        )
    }

    override fun viewFor(state: GameState, viewer: Viewer): GameView {
        val s = state as MonoChaosState
        val ownHand = when (viewer) {
            is Viewer.Player -> s.handForSeat(viewer.seat.value)
            Viewer.Spectator -> emptyList()
        }
        val opponentCounts = s.seats.map { it.value }.associate { seat ->
            seat to s.cardCountForSeat(seat)
        }
        return MonoChaosView(
            seats = s.seats.map { it.value },
            currentSeat = s.currentSeat,
            phase = s.phase.name,
            topCard = s.discardPile.lastOrNull(),
            topCardColor = s.topCardColor,
            hand = ownHand,
            opponentCardCounts = opponentCounts,
            drawPileCount = s.drawPile.size,
            discardPileCount = s.discardPile.size,
            monoCallWindowExpiry = s.monoCallWindowExpiry,
            activeStackPenalty = s.activeStackPenalty,
            eliminatedSeats = s.eliminatedSeats,
        )
    }

    override fun isFinished(state: GameState) = (state as MonoChaosState).phase == MonoChaosPhase.FINISHED

    override fun outcome(state: GameState): MatchOutcome {
        val s = state as MonoChaosState
        val ranking = mutableListOf<RankedSeat>()
        val winner = s.seats.firstOrNull { s.cardCountForSeat(it.value) == 0 && !s.isEliminated(it.value) }
        if (winner != null) {
            ranking.add(RankedSeat(winner, SeatStatus.FINISHED, 1))
        }
        val remaining = s.seats.map { it.value }
            .filter { it != winner?.value && !s.isEliminated(it) }
            .sortedBy { s.cardCountForSeat(it) }
        for ((index, seat) in remaining.withIndex()) {
            ranking.add(RankedSeat(SeatNumber(seat), SeatStatus.FINISHED, index + 2))
        }
        for (seat in s.eliminatedSeats.sorted()) {
            ranking.add(RankedSeat(SeatNumber(seat), SeatStatus.REMOVED, ranking.size + 1))
        }
        return MatchOutcome(ranking)
    }

    private fun isPlayable(card: MonoChaosCard, topCard: MonoChaosCard?, topColor: Color?): Boolean {
        if (card.isPrism()) return true
        val effectiveColor = topColor ?: topCard?.playColor()
        if (effectiveColor == null) return false
        return when (card) {
            is MonoChaosCard.NumberCard -> card.color == effectiveColor || card.number == (topCard as? MonoChaosCard.NumberCard)?.number
            is MonoChaosCard.ReverseCard -> card.color == effectiveColor || topCard is MonoChaosCard.ReverseCard
            is MonoChaosCard.SkipCard -> card.color == effectiveColor || topCard is MonoChaosCard.SkipCard
            is MonoChaosCard.DrawTwoCard -> card.color == effectiveColor || topCard is MonoChaosCard.DrawTwoCard
            is MonoChaosCard.ColoredDrawFourCard -> card.color == effectiveColor || topCard is MonoChaosCard.ColoredDrawFourCard
            is MonoChaosCard.DiscardAllCard -> card.color == effectiveColor || topCard is MonoChaosCard.DiscardAllCard
            is MonoChaosCard.SkipEveryoneCard -> card.color == effectiveColor || topCard is MonoChaosCard.SkipEveryoneCard
            else -> false
        }
    }

    private fun handlePlayCard(s: MonoChaosState, seat: SeatNumber, handIndex: Int, random: GameRandom): ApplyResult {
        val hand = s.handForSeat(seat.value)
        if (handIndex !in hand.indices) {
            return ApplyResult.Rejected(TypedRejection("BAD_CARD", "No card at that position"))
        }
        val card = hand[handIndex]
        // An illegal move is the client's mistake, not a server crash.
        if (!isPlayable(card, s.discardPile.lastOrNull(), s.topCardColor)) {
            return ApplyResult.Rejected(TypedRejection("NOT_PLAYABLE", "Card does not match"))
        }

        val events = mutableListOf<MonoChaosEvent>()
        val newHands = s.hands.toMutableMap()
        val newHand = hand.toMutableList()
        newHand.removeAt(handIndex)
        newHands[seat.value] = newHand

        val newDiscard = s.discardPile.toMutableList()
        newDiscard.add(card)

        var newTopColor = card.playColor()
        var newPhase = MonoChaosPhase.AWAITING_PLAY
        var nextSeat = s.currentSeat
        var newStackPenalty = s.activeStackPenalty

        when (card) {
            is MonoChaosCard.PrismColorWild, is MonoChaosCard.PrismDrawFour,
            is MonoChaosCard.PrismReverseDrawFour, is MonoChaosCard.PrismDrawSix,
            is MonoChaosCard.PrismDrawTen, is MonoChaosCard.PrismColorRoulette -> {
                newPhase = MonoChaosPhase.AWAITING_COLOR_CHOICE
            }
            is MonoChaosCard.DrawTwoCard, is MonoChaosCard.ColoredDrawFourCard -> {
                newStackPenalty = card.penaltyValue()
                newPhase = MonoChaosPhase.AWAITING_STACK_CHOICE
            }
            is MonoChaosCard.DiscardAllCard -> {
                val color = card.color!!
                val kept = newHand.filter { c ->
                    !(c is MonoChaosCard.NumberCard && c.color == color) &&
                        !(c is MonoChaosCard.ReverseCard && c.color == color) &&
                        !(c is MonoChaosCard.SkipCard && c.color == color) &&
                        !(c is MonoChaosCard.DrawTwoCard && c.color == color)
                }
                newHands[seat.value] = kept
                newTopColor = color
                nextSeat = nextActiveSeat(s, seat.value)
            }
            is MonoChaosCard.SkipEveryoneCard -> {
                newTopColor = card.color
                nextSeat = seat.value
            }
            else -> {
                newTopColor = card.playColor()
                nextSeat = nextActiveSeat(s, seat.value)
            }
        }

        var newEliminated = s.eliminatedSeats
        if (newHand.size >= MonoChaosConstants.KNOCKOUT_THRESHOLD) {
            newEliminated = newEliminated + seat.value
        }

        val newState = s.copy(
            hands = newHands,
            drawPile = s.drawPile,
            discardPile = newDiscard.toList(),
            currentSeat = nextSeat,
            phase = newPhase,
            topCardColor = newTopColor,
            turnCounter = s.turnCounter + 1,
            lastActionSeat = seat.value,
            lastActionCard = card,
            activeStackPenalty = newStackPenalty,
            eliminatedSeats = newEliminated,
        )

        events.add(MonoChaosEvent.CardPlayed(seat, card, newTopColor))

        if (newHand.isEmpty()) {
            return ApplyResult.Success(newState.copy(
                phase = MonoChaosPhase.FINISHED,
                turn = MonoChaosTurnState(SeatNumber(nextSeat), MonoChaosPhase.FINISHED.name),
            ), events + MonoChaosEvent.SeatFinished(seat, 1) + MonoChaosEvent.MatchEnded(listOf(seat.value)))
        }

        val activeNonEliminated = newState.seats.map { it.value }.filter { !newState.isEliminated(it) }
        if (activeNonEliminated.size <= 1) {
            return ApplyResult.Success(newState.copy(
                phase = MonoChaosPhase.FINISHED,
            ), events + MonoChaosEvent.MatchEnded(activeNonEliminated))
        }

        return ApplyResult.Success(newState, events)
    }

    private fun handleDrawUntilPlayable(s: MonoChaosState, seat: SeatNumber, random: GameRandom): ApplyResult {
        val events = mutableListOf<MonoChaosEvent>()
        val newHands = s.hands.toMutableMap()
        var newHand = s.handForSeat(seat.value).toMutableList()
        var newDrawPile = s.drawPile.toMutableList()
        var drawnCount = 0

        while (newDrawPile.isNotEmpty()) {
            val card = newDrawPile.removeAt(0)
            newHand.add(card)
            drawnCount++
            if (isPlayable(card, s.discardPile.lastOrNull(), s.topCardColor)) break
            if (newHand.size >= MonoChaosConstants.KNOCKOUT_THRESHOLD) break
        }

        newHands[seat.value] = newHand

        var newEliminated = s.eliminatedSeats
        if (newHand.size >= MonoChaosConstants.KNOCKOUT_THRESHOLD) {
            newEliminated = newEliminated + seat.value
        }

        val nextSeat = nextActiveSeat(s, seat.value)
        val newState = s.copy(
            hands = newHands,
            drawPile = newDrawPile,
            currentSeat = nextSeat,
            phase = MonoChaosPhase.AWAITING_PLAY,
            topCardColor = s.topCardColor,
            turnCounter = s.turnCounter + 1,
            lastActionSeat = seat.value,
            lastActionCard = null,
            eliminatedSeats = newEliminated,
        )

        events.add(MonoChaosEvent.CardsDrawn(seat, drawnCount))
        events.add(MonoChaosEvent.TurnPassed(seat))

        return ApplyResult.Success(newState, events)
    }

    private fun handleChooseColor(s: MonoChaosState, seat: SeatNumber, color: Color, random: GameRandom): ApplyResult {
        if (s.phase != MonoChaosPhase.AWAITING_COLOR_CHOICE) {
            return ApplyResult.Rejected(TypedRejection("NOT_WAITING", "Not choosing a colour"))
        }
        val nextSeat = nextActiveSeat(s, seat.value)
        val newState = s.copy(
            topCardColor = color,
            currentSeat = nextSeat,
            phase = MonoChaosPhase.AWAITING_PLAY,
            turnCounter = s.turnCounter + 1,
            lastActionSeat = seat.value,
        )
        return ApplyResult.Success(newState, listOf(MonoChaosEvent.ColorChosen(seat, color)))
    }

    private fun handleChooseRouletteColor(s: MonoChaosState, seat: SeatNumber, color: Color, random: GameRandom): ApplyResult {
        val nextSeat = nextActiveSeat(s, seat.value)
        val newState = s.copy(
            topCardColor = color,
            currentSeat = nextSeat,
            phase = MonoChaosPhase.AWAITING_PLAY,
            turnCounter = s.turnCounter + 1,
            lastActionSeat = seat.value,
        )
        return ApplyResult.Success(newState, listOf(MonoChaosEvent.RouletteColorChosen(seat, color)))
    }

    private fun handleCallMono(s: MonoChaosState, seat: SeatNumber, random: GameRandom): ApplyResult {
        if (s.monoCallWindowExpiry <= System.currentTimeMillis()) {
            return ApplyResult.Rejected(TypedRejection("WINDOW_CLOSED", "MONO! window not open"))
        }
        if (s.cardCountForSeat(seat.value) != 1) {
            return ApplyResult.Rejected(TypedRejection("NOT_ONE_CARD", "Call MONO! with one card"))
        }
        val newState = s.copy(
            monoCallWindowExpiry = 0,
            lastActionSeat = seat.value,
        )
        return ApplyResult.Success(newState, listOf(MonoChaosEvent.MonoCalled(seat)))
    }

    private fun handleStackDraw(s: MonoChaosState, seat: SeatNumber, handIndex: Int, random: GameRandom): ApplyResult {
        val hand = s.handForSeat(seat.value)
        if (handIndex !in hand.indices) {
            return ApplyResult.Rejected(TypedRejection("BAD_CARD", "No card at that position"))
        }
        val card = hand[handIndex]
        if (card.penaltyValue() < s.activeStackPenalty || card.penaltyValue() <= 0) {
            return ApplyResult.Rejected(TypedRejection("BAD_STACK", "Cannot stack this card"))
        }

        val events = mutableListOf<MonoChaosEvent>()
        val newHands = s.hands.toMutableMap()
        val newHand = hand.toMutableList()
        newHand.removeAt(handIndex)
        newHands[seat.value] = newHand

        val newDiscard = s.discardPile.toMutableList()
        newDiscard.add(card)

        val newStackPenalty = card.penaltyValue()
        val nextSeat = nextActiveSeat(s, seat.value)

        val newState = s.copy(
            hands = newHands,
            discardPile = newDiscard.toList(),
            currentSeat = nextSeat,
            phase = MonoChaosPhase.AWAITING_STACK_CHOICE,
            topCardColor = card.playColor(),
            activeStackPenalty = newStackPenalty,
            turnCounter = s.turnCounter + 1,
            lastActionSeat = seat.value,
            lastActionCard = card,
        )

        events.add(MonoChaosEvent.StackDraw(seat, card, newStackPenalty))
        return ApplyResult.Success(newState, events)
    }

    private fun handleAcceptStack(s: MonoChaosState, seat: SeatNumber, random: GameRandom): ApplyResult {
        if (s.activeStackPenalty <= 0) {
            return ApplyResult.Rejected(TypedRejection("NO_STACK", "No penalty to accept"))
        }
        val penalty = s.activeStackPenalty
        val events = mutableListOf<MonoChaosEvent>()

        val newHands = s.hands.toMutableMap()
        var newHand = s.handForSeat(seat.value).toMutableList()
        var newDrawPile = s.drawPile.toMutableList()
        var drawn = 0

        for (i in 0 until penalty) {
            if (newDrawPile.isNotEmpty()) {
                newHand.add(newDrawPile.removeAt(0))
                drawn++
            }
        }

        newHands[seat.value] = newHand

        var newEliminated = s.eliminatedSeats
        if (newHand.size >= MonoChaosConstants.KNOCKOUT_THRESHOLD) {
            newEliminated = newEliminated + seat.value
        }

        val nextSeat = nextActiveSeat(s, seat.value)
        val newState = s.copy(
            hands = newHands,
            drawPile = newDrawPile,
            currentSeat = nextSeat,
            phase = MonoChaosPhase.AWAITING_PLAY,
            topCardColor = s.topCardColor,
            activeStackPenalty = 0,
            turnCounter = s.turnCounter + 1,
            lastActionSeat = seat.value,
            eliminatedSeats = newEliminated,
        )

        events.add(MonoChaosEvent.StackAccepted(seat, penalty))
        events.add(MonoChaosEvent.TurnPassed(seat))

        if (newHand.size >= MonoChaosConstants.KNOCKOUT_THRESHOLD) {
            events.add(MonoChaosEvent.PlayerEliminated(seat))
        }

        return ApplyResult.Success(newState, events)
    }

    private fun nextActiveSeat(s: MonoChaosState, currentSeat: Int): Int {
        val activeSeats = s.seats.map { it.value }.filter { !s.isEliminated(it) }
        if (activeSeats.isEmpty()) return currentSeat
        val currentIndex = activeSeats.indexOf(currentSeat)
        return if (currentIndex >= 0) activeSeats[(currentIndex + 1) % activeSeats.size] else activeSeats.first()
    }
}
