package com.kglabs28.yapnplay.appengine.games.royalludo.engine

import com.kglabs28.yapnplay.appengine.games.base.Game
import com.kglabs28.yapnplay.appengine.games.base.identity.*
import com.kglabs28.yapnplay.appengine.games.common.gameplay.moves.dice.RollDice
import com.kglabs28.yapnplay.appengine.games.common.gameplay.moves.pieces.MovePiece
import com.kglabs28.yapnplay.appengine.games.common.gameplay.moves.pieces.PieceId
import com.kglabs28.yapnplay.appengine.games.base.match.*
import com.kglabs28.yapnplay.appengine.games.base.play.*
import com.kglabs28.yapnplay.appengine.games.base.rules.*
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.*
import kotlinx.serialization.Serializable

class RoyalLudoEngine : Game {
    override val id = GameId("royal-ludo")
    override val displayName = "Royal Ludo"
    override val timing = TimingConfig()
    override val minPlayers = 2
    override val maxPlayers = 4
    override val maxSpectators: Int? = null
    override val turnPolicy: TurnPolicy = TurnPolicy.Sequential

    override fun defaultRules(): GameRules = RoyalLudoRules.TRADITIONAL

    override fun rulesSummary(): String =
        "Traditional Royal Ludo for 2-4 players: leave base on a 6, extra turn " +
            "on a 6, a capture or a finished piece, three sixes forfeit, exact " +
            "roll to finish, lone pieces are captured except on safe squares, " +
            "pairs of two or more same-colour pieces are protected."

    override fun start(rules: GameRules, seatCount: Int, random: GameRandom): GameState {
        require(seatCount in 2..4) { "Royal Ludo requires 2-4 players" }
        val ludoRules = rules as? RoyalLudoRules ?: RoyalLudoRules.TRADITIONAL
        val colours = (0..3).shuffled(random.asKotlinRandom())
        val seatColours = (1..seatCount).associateWith { colours[it - 1] }
        val pieces = (1..seatCount).associateWith { List(ludoRules.mandatory.piecesPerColour) { PROGRESS_IN_BASE } }
        val seats = (1..seatCount).map { SeatNumber(it) }
        return RoyalLudoState(
            _gameId = id, _rules = ludoRules, _seats = seats,
            _turn = RoyalLudoTurnState(SeatNumber(1), Phase.AWAITING_ROLL.name),
            seatColours = seatColours, pieces = pieces, currentSeat = 1, phase = Phase.AWAITING_ROLL,
        )
    }

    override fun apply(state: GameState, seat: SeatNumber, action: Action, random: GameRandom): ApplyResult {
        val s = state as RoyalLudoState
        if (s.phase == Phase.FINISHED) return ApplyResult.Rejected(TypedRejection("MATCH_ENDED", "Match ended"))
        if (seat.value != s.currentSeat) return ApplyResult.Rejected(TypedRejection("NOT_YOUR_TURN", "Not your turn"))
        return when (action) {
            is RollDice -> handleRoll(s, random)
            is MovePiece -> handleMove(s, action.pieceId.value.toInt())
            else -> ApplyResult.Rejected(TypedRejection("BAD_REQUEST", "Unknown action for Royal Ludo"))
        }
    }

    override fun currentActor(state: GameState) = SeatNumber((state as RoyalLudoState).currentSeat)

    override fun autoplay(state: GameState, seat: SeatNumber): ApplyResult {
        val s = state as RoyalLudoState
        if (s.phase == Phase.FINISHED) return ApplyResult.Rejected(TypedRejection("MATCH_ENDED", "Match ended"))
        if (seat.value != s.currentSeat) return ApplyResult.Rejected(TypedRejection("NOT_YOUR_TURN", "Not your turn"))
        return when (s.phase) {
            Phase.AWAITING_ROLL -> {
                // Deterministic roll derived from the turn counter: the same
                // state always yields the same roll, and it varies turn to turn.
                val roll = kotlin.random.Random(state.turnCounter).nextInt(1, 7)
                handleRoll(s, FixedRandom(roll))
            }
            Phase.AWAITING_MOVE -> chooseBestMove(s)
            Phase.FINISHED -> ApplyResult.Rejected(TypedRejection("MATCH_ENDED", "Match ended"))
        }
    }

    override fun removeSeat(state: GameState, seat: SeatNumber): GameState {
        val s = state as RoyalLudoState
        if (seat.value !in s.seats.map { it.value }) return s
        val newSeats = s.seats.filter { it.value != seat.value }
        val newRemovalOrder = s.removalOrder + seat.value
        var newCurrentSeat = s.currentSeat
        if (seat.value == s.currentSeat) {
            newSeats.firstOrNull()?.let { newCurrentSeat = it.value } ?: return s.copy(
                seats = newSeats, removalOrder = newRemovalOrder, phase = Phase.FINISHED,
                turn = RoyalLudoTurnState(SeatNumber(1), Phase.FINISHED.name),
            )
        }
        return s.copy(
            seats = newSeats, removalOrder = newRemovalOrder, currentSeat = newCurrentSeat,
            phase = if (newSeats.size <= 1) Phase.FINISHED else s.phase,
            turn = RoyalLudoTurnState(SeatNumber(newCurrentSeat), s.phase.name),
        )
    }

    override fun viewFor(state: GameState, viewer: Viewer): GameView {
        val s = state as RoyalLudoState
        val seatViews = s.seats.map { sn ->
            val seat = sn.value
            RoyalLudoSeatView(
                seat = seat, colour = s.colourForSeat(seat), pieces = s.piecesForSeat(seat),
                status = when {
                    seat in s.removalOrder -> "REMOVED"
                    seat in s.finishingOrder -> "FINISHED"
                    else -> "ACTIVE"
                },
                finished = seat in s.finishingOrder,
            )
        }
        val legalMoves = when (viewer) {
            is Viewer.Player -> if (viewer.seat.value == s.currentSeat && s.phase == Phase.AWAITING_MOVE) s.legalPieces else emptyList()
            Viewer.Spectator -> emptyList()
        }
        return RoyalLudoView(
            seats = seatViews, currentSeat = s.currentSeat, phase = s.phase.name,
            lastRoll = s.lastRoll, legalMoves = legalMoves,
            finishingOrder = s.finishingOrder, turnCounter = s.turnCounter,
        )
    }

    override fun isFinished(state: GameState) = (state as RoyalLudoState).phase == Phase.FINISHED

    override fun outcome(state: GameState): MatchOutcome {
        val s = state as RoyalLudoState
        val ranking = mutableListOf<RankedSeat>()
        for ((index, seat) in s.finishingOrder.withIndex())
            ranking.add(RankedSeat(SeatNumber(seat), SeatStatus.FINISHED, index + 1))
        for (seat in s.seats.map { it.value }.filter { it !in s.finishingOrder })
            ranking.add(RankedSeat(SeatNumber(seat), SeatStatus.FINISHED, ranking.size + 1))
        for (seat in s.removalOrder.reversed())
            ranking.add(RankedSeat(SeatNumber(seat), SeatStatus.REMOVED, ranking.size + 1))
        return MatchOutcome(ranking)
    }

    private fun handleRoll(state: RoyalLudoState, random: GameRandom): ApplyResult {
        val roll = random.nextInt(6) + 1
        val events = mutableListOf<GameEvent>()
        if (state.ludoRules.house.threeSixesForfeit && roll == 6 && state.consecutiveSixes >= MAX_CONSECUTIVE_SIXES - 1) {
            events.add(DiceRolled(state.currentSeat, roll))
            events.add(TurnForfeited(state.currentSeat, "THREE_SIXES"))
            val newState = state.copy(consecutiveSixes = 0, lastRoll = roll, phase = Phase.AWAITING_ROLL, legalPieces = emptyList(), turnCounter = state.turnCounter + 1)
            return ApplyResult.Success(advanceTurn(newState, events), events)
        }
        val newConsecutiveSixes = if (roll == 6) state.consecutiveSixes + 1 else 0
        val legalPieces = computeLegalPieces(state, roll)
        events.add(DiceRolled(state.currentSeat, roll))
        val newState = state.copy(lastRoll = roll, legalPieces = legalPieces, consecutiveSixes = newConsecutiveSixes, phase = if (legalPieces.isNotEmpty()) Phase.AWAITING_MOVE else Phase.AWAITING_ROLL, turnCounter = state.turnCounter + 1)
        if (legalPieces.isEmpty()) {
            events.add(TurnPassed(state.currentSeat, "NO_LEGAL_MOVE"))
            return ApplyResult.Success(advanceTurn(newState, events), events)
        }
        if (state.ludoRules.house.autoMoveWhenSingleOption && legalPieces.size == 1) return handleMove(newState, legalPieces.first())
        return ApplyResult.Success(newState, events)
    }

    private fun handleMove(state: RoyalLudoState, piece: Int): ApplyResult {
        val s = state
        val events = mutableListOf<GameEvent>()
        if (piece !in s.legalPieces) return ApplyResult.Rejected(TypedRejection("ILLEGAL_MOVE", "Piece $piece cannot move"))
        val colour = s.colourForSeat(s.currentSeat)
        val startSquare = RoyalLudoBoardUtils.startSquareFor(colour)
        val currentProgress = s.pieceProgress(s.currentSeat, piece)
        val roll = s.lastRoll ?: return ApplyResult.Rejected(TypedRejection("BAD_REQUEST", "No roll"))

        val newProgress: Int
        val squaresPassed: Int
        when {
            currentProgress == PROGRESS_IN_BASE -> {
                if (roll !in s.ludoRules.house.baseExitRolls) return ApplyResult.Rejected(TypedRejection("ILLEGAL_MOVE", "Need ${s.ludoRules.house.baseExitRolls} to leave base"))
                newProgress = 0; squaresPassed = 0
            }
            currentProgress + roll == PROGRESS_FINISH -> { newProgress = PROGRESS_FINISH; squaresPassed = roll }
            s.ludoRules.mandatory.exactFinish && currentProgress + roll > PROGRESS_FINISH -> return ApplyResult.Rejected(TypedRejection("ILLEGAL_MOVE", "Need exact roll"))
            currentProgress + roll <= PROGRESS_FINISH -> { newProgress = currentProgress + roll; squaresPassed = roll }
            else -> return ApplyResult.Rejected(TypedRejection("ILLEGAL_MOVE", "Exceeds finish"))
        }

        val newPieces = s.pieces.toMutableMap()
        val seatPieces = newPieces[s.currentSeat]!!.toMutableList()
        seatPieces[piece] = newProgress
        newPieces[s.currentSeat] = seatPieces

        events.add(PieceMoved(s.currentSeat, piece, currentProgress, newProgress, squaresPassed))

        var captured = false
        if (s.ludoRules.mandatory.capturesSendToBase && newProgress in 0..PROGRESS_ENTER_HOME) {
            val absSquare = RoyalLudoBoardUtils.absoluteSquare(startSquare, newProgress)
            if (!RoyalLudoBoardUtils.isSafeSquare(absSquare)) {
                val opposingSeat = findOpposingPiece(s, absSquare, colour)
                if (opposingSeat != null) {
                    val opposingColour = s.colourForSeat(opposingSeat)
                    val opposingCount = countPiecesOnSquare(s, absSquare, opposingColour)
                    // Protected pair (2+) is never captured; with pair
                    // protection off, one piece of the group is captured.
                    val canCapture = if (s.ludoRules.house.pairProtection) opposingCount == 1 else opposingCount >= 1
                    if (canCapture) {
                        val victimPiece = findPieceOnSquare(s, absSquare, opposingSeat)
                        val victimPieces = newPieces[opposingSeat]!!.toMutableList()
                        victimPieces[victimPiece] = PROGRESS_IN_BASE
                        newPieces[opposingSeat] = victimPieces
                        events.add(PieceCaptured(s.currentSeat, opposingSeat, victimPiece, "CAPTURE"))
                        captured = true
                    }
                }
            }
        }

        val finished = newProgress == PROGRESS_FINISH
        if (finished) events.add(PieceFinished(s.currentSeat, piece))

        val extraTurn = when {
            captured && s.ludoRules.house.extraTurnOnCapture -> true
            finished && s.ludoRules.house.extraTurnOnHome -> true
            roll == 6 && s.ludoRules.house.extraTurnOnSix -> true
            else -> false
        }
        if (extraTurn) events.add(ExtraTurn(s.currentSeat, when { captured -> "CAPTURE"; finished -> "HOME"; else -> "SIX" }))

        val allFinished = newPieces[s.currentSeat]!!.all { it == PROGRESS_FINISH }
        val newFinishingOrder = if (allFinished && s.currentSeat !in s.finishingOrder) {
            events.add(SeatFinished(s.currentSeat, s.finishingOrder.size + 1))
            s.finishingOrder + s.currentSeat
        } else s.finishingOrder

        val activeSeats = s.seats.map { it.value }.filter { it !in newFinishingOrder }
        val matchOver = activeSeats.size <= 1 && activeSeats.none { it !in s.removalOrder }

        val nextSeat = if (!extraTurn && !matchOver) nextActiveSeat(s, newFinishingOrder) else s.currentSeat
        val newState = s.copy(
            pieces = newPieces, lastRoll = null,
            legalPieces = if (extraTurn) emptyList() else s.legalPieces,
            consecutiveSixes = if (roll == 6 && !extraTurn) 0 else s.consecutiveSixes,
            finishingOrder = newFinishingOrder, phase = if (matchOver) Phase.FINISHED else Phase.AWAITING_ROLL,
            turnCounter = s.turnCounter + 1,
            turn = RoyalLudoTurnState(SeatNumber(nextSeat), if (matchOver) Phase.FINISHED.name else Phase.AWAITING_ROLL.name),
            currentSeat = nextSeat,
        )
        val finalEvents = if (matchOver) events + MatchEnded(newFinishingOrder + s.removalOrder) else events
        return ApplyResult.Success(newState, finalEvents)
    }

    private fun computeLegalPieces(state: RoyalLudoState, roll: Int): List<Int> {
        val legal = mutableListOf<Int>()
        for (piece in 0 until state.ludoRules.mandatory.piecesPerColour) {
            val progress = state.pieceProgress(state.currentSeat, piece)
            when {
                progress == PROGRESS_IN_BASE -> if (roll in state.ludoRules.house.baseExitRolls) legal.add(piece)
                progress == PROGRESS_FINISHED -> {}
                progress + roll == PROGRESS_FINISH -> legal.add(piece)
                state.ludoRules.mandatory.exactFinish && progress + roll > PROGRESS_FINISH -> {}
                progress + roll < PROGRESS_FINISH -> legal.add(piece)
            }
        }
        return legal
    }

    private fun findOpposingPiece(state: RoyalLudoState, absSquare: Int, myColour: Int): Int? {
        for ((seat, colour) in state.seatColours) {
            if (colour == myColour || seat in state.removalOrder) continue
            val startSq = RoyalLudoBoardUtils.startSquareFor(colour)
            for ((idx, progress) in state.piecesForSeat(seat).withIndex())
                if (progress in 0..PROGRESS_ENTER_HOME && RoyalLudoBoardUtils.absoluteSquare(startSq, progress) == absSquare) return seat
        }
        return null
    }

    private fun countPiecesOnSquare(state: RoyalLudoState, absSquare: Int, colour: Int): Int {
        val startSq = RoyalLudoBoardUtils.startSquareFor(colour)
        return state.seatColours.filter { it.value == colour }.keys.sumOf { seat ->
            state.piecesForSeat(seat).count { it in 0..PROGRESS_ENTER_HOME && RoyalLudoBoardUtils.absoluteSquare(startSq, it) == absSquare }
        }
    }

    private fun findPieceOnSquare(state: RoyalLudoState, absSquare: Int, seat: Int): Int {
        val startSq = RoyalLudoBoardUtils.startSquareFor(state.colourForSeat(seat))
        return state.piecesForSeat(seat).withIndex().first { it.value in 0..PROGRESS_ENTER_HOME && RoyalLudoBoardUtils.absoluteSquare(startSq, it.value) == absSquare }.index
    }

    private fun advanceTurn(state: RoyalLudoState, events: MutableList<GameEvent>): RoyalLudoState {
        val nextSeat = nextActiveSeat(state, state.finishingOrder)
        return state.copy(currentSeat = nextSeat, phase = Phase.AWAITING_ROLL, legalPieces = emptyList(), lastRoll = null, turnCounter = state.turnCounter + 1, turn = RoyalLudoTurnState(SeatNumber(nextSeat), Phase.AWAITING_ROLL.name))
    }

    private fun nextActiveSeat(state: RoyalLudoState, finishingOrder: List<Int>): Int {
        val activeSeats = state.seats.map { it.value }.filter { it !in finishingOrder && it !in state.removalOrder }
        if (activeSeats.isEmpty()) return state.currentSeat
        val currentIndex = activeSeats.indexOf(state.currentSeat)
        return if (currentIndex >= 0) activeSeats[(currentIndex + 1) % activeSeats.size] else activeSeats.first()
    }

    private fun chooseBestMove(state: RoyalLudoState): ApplyResult {
        val bestPiece = state.legalPieces.maxByOrNull { scoreMove(state, it) }
            ?: return ApplyResult.Rejected(TypedRejection("NO_LEGAL_MOVE", "No legal move"))
        return handleMove(state, bestPiece)
    }

    private fun scoreMove(state: RoyalLudoState, piece: Int): Int {
        val progress = state.pieceProgress(state.currentSeat, piece)
        val roll = state.lastRoll ?: return 0
        val colour = state.colourForSeat(state.currentSeat)
        val startSquare = RoyalLudoBoardUtils.startSquareFor(colour)
        var score = 0
        if (progress + roll == PROGRESS_FINISH) score += SCORE_FINISH
        if (progress == PROGRESS_IN_BASE) score += SCORE_LEAVE_BASE
        val newProgress = when {
            progress == PROGRESS_IN_BASE -> 0
            progress + roll <= PROGRESS_FINISH -> progress + roll
            else -> progress
        }
        if (newProgress in 0..PROGRESS_ENTER_HOME) {
            val absSquare = RoyalLudoBoardUtils.absoluteSquare(startSquare, newProgress)
            if (!RoyalLudoBoardUtils.isSafeSquare(absSquare)) {
                val opposingSeat = findOpposingPiece(state, absSquare, colour)
                if (opposingSeat != null) {
                    val opposingColour = state.colourForSeat(opposingSeat)
                    if (countPiecesOnSquare(state, absSquare, opposingColour) == 1) {
                        val victimProgress = state.piecesForSeat(opposingSeat).maxOrNull() ?: 0
                        score += SCORE_CAPTURE + victimProgress
                    }
                }
            }
            if (RoyalLudoBoardUtils.isSafeSquare(absSquare) || countPiecesOnSquare(state, absSquare, colour) >= 1) score += SCORE_SAFE
        }
        score += newProgress - piece
        return score
    }
}

@Serializable
data class RoyalLudoSeatView(val seat: Int, val colour: Int, val pieces: List<Int>, val status: String, val finished: Boolean)
@Serializable
data class RoyalLudoView(val seats: List<RoyalLudoSeatView>, val currentSeat: Int, val phase: String, val lastRoll: Int?, val legalMoves: List<Int>, val finishingOrder: List<Int>, val turnCounter: Long) : GameView

/** Test-only fixed dice face (1-6). */
private class FixedRandom(private val roll: Int) : GameRandom {
    override fun nextInt(bound: Int): Int = (roll - 1) % bound
}

private fun GameRandom.asKotlinRandom(): kotlin.random.Random = object : kotlin.random.Random() {
    override fun nextBits(bitCount: Int) = this@asKotlinRandom.nextInt(1 shl bitCount)
    override fun nextInt() = this@asKotlinRandom.nextInt(Int.MAX_VALUE)
    override fun nextInt(until: Int) = this@asKotlinRandom.nextInt(until)
    override fun nextInt(from: Int, until: Int) = this@asKotlinRandom.nextInt(until - from) + from
    override fun nextBoolean() = this@asKotlinRandom.nextInt(2) == 0
    override fun nextDouble() = this@asKotlinRandom.nextInt(Int.MAX_VALUE).toDouble() / Int.MAX_VALUE
    override fun nextLong() = this@asKotlinRandom.nextInt(Int.MAX_VALUE).toLong()
}
