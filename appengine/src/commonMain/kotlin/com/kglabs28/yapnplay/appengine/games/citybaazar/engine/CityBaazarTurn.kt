package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber
import com.kglabs28.yapnplay.appengine.games.base.play.ApplyResult
import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CLUB_FEE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CardEffect
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoard
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarCards
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.DICE_FACES
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.HOTEL_LEVEL
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.JAIL_FINE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.JAIL_TILE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.MAX_DOUBLES
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.MAX_JAIL_TRIES
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.ROUND_LIMIT
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TILE_COUNT
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.MoneyReason

/**
 * One action's worth of play: works on a copy of the state and collects
 * the events. A move ends in a decision (BUY / BUILD / DEBT), another roll
 * (doubles) or the next player's turn.
 */
internal class CityBaazarTurn(private var s: CityBaazarState, private val random: GameRandom) {
    private val events = mutableListOf<GameEvent>()
    /** The turn passed on during this action (nothing else may move it on). */
    private var ended = false

    fun result(): ApplyResult = ApplyResult.Success(s, events.toList())

    // ---------------- rolling and moving ----------------

    fun roll() {
        val seat = s.currentSeat
        val d1 = random.nextInt(DICE_FACES) + 1
        val d2 = random.nextInt(DICE_FACES) + 1
        val total = d1 + d2
        events += DiceRolled(seat, d1, d2)
        s = s.copy(die1 = d1, die2 = d2, turnCounter = s.turnCounter + 1)
        if (s.inJail(seat)) return jailRoll(seat, d1 == d2, total)
        if (d1 == d2) {
            val run = s.doublesInRow + 1
            if (run >= MAX_DOUBLES) return goToJail(seat)
            s = s.copy(doublesInRow = run, rollAgain = true)
        } else {
            s = s.copy(rollAgain = false)
        }
        moveSteps(seat, total, total)
        settle()
    }

    private fun jailRoll(seat: Int, doubles: Boolean, total: Int) {
        s = s.copy(rollAgain = false)
        if (doubles) {
            leaveJail(seat, "DOUBLES")
            moveSteps(seat, total, total)
            return settle()
        }
        val tries = (s.jailTries[seat] ?: 0) + 1
        if (tries < MAX_JAIL_TRIES) {
            s = s.copy(jailTries = s.jailTries + (seat to tries))
            return nextTurn()
        }
        // Third failed try: pay the fine, then move by this roll.
        leaveJail(seat, "FINE")
        if (charge(seat, JAIL_FINE, emptyMap(), MoneyReason.JAIL_FINE, JAIL_TILE)) {
            moveSteps(seat, total, total)
            settle()
        } else {
            s = s.copy(pendingMove = total)
        }
    }

    private fun moveSteps(seat: Int, steps: Int, diceTotal: Int) {
        val from = s.positions[seat] ?: 0
        val raw = from + steps
        val to = raw % TILE_COUNT
        events += TokenMoved(seat, from, to, steps, jump = false)
        s = s.copy(positions = s.positions + (seat to to))
        if (raw >= TILE_COUNT) receive(seat, s.cbRules.mandatory.startBonus, MoneyReason.START, null)
        land(seat, to, diceTotal)
    }

    private fun land(seat: Int, tile: Int, diceTotal: Int) {
        val spec = CityBaazarBoard.tile(tile)
        when (spec.kind) {
            TileKind.START, TileKind.JAIL -> Unit
            TileKind.CLUB -> if (s.cbRules.house.clubFee) charge(seat, CLUB_FEE, emptyMap(), MoneyReason.CLUB, tile)
            TileKind.REST_HOUSE -> if (s.cbRules.house.restHouseSkips) {
                s = s.copy(skipNext = s.skipNext + seat)
                events += RestTaken(seat)
            }
            TileKind.TAX -> charge(seat, spec.tax, emptyMap(), MoneyReason.TAX, tile)
            TileKind.LUCK -> drawCard(seat, CardDeck.LUCK, diceTotal)
            TileKind.CHEST -> drawCard(seat, CardDeck.CHEST, diceTotal)
            TileKind.CITY, TileKind.TRANSIT, TileKind.UTILITY -> {
                val owner = s.owners[tile]
                when {
                    owner == null -> s = s.copy(phase = CityBaazarPhase.BUY, pendingTile = tile)
                    owner == seat -> if (CityBaazarEconomy.buildAllowed(s, seat, tile) && s.cashOf(seat) >= spec.buildCost) {
                        s = s.copy(phase = CityBaazarPhase.BUILD, pendingTile = tile)
                    }
                    else -> {
                        val rent = CityBaazarEconomy.rent(s, tile, diceTotal)
                        if (rent > 0) charge(seat, rent, mapOf(owner to rent), MoneyReason.RENT, tile)
                    }
                }
            }
        }
    }

    private fun drawCard(seat: Int, deck: CardDeck, diceTotal: Int) {
        val cards = CityBaazarCards.deck(deck)
        val card = cards[random.nextInt(cards.size)]
        events += CardDrawn(seat, card.id)
        when (val e = card.effect) {
            is CardEffect.Money -> if (e.amount >= 0) {
                receive(seat, e.amount, MoneyReason.CARD, null)
            } else {
                charge(seat, -e.amount, emptyMap(), MoneyReason.CARD, null)
            }
            is CardEffect.AdvanceTo -> {
                val from = s.positions[seat] ?: 0
                val steps = (e.tile - from + TILE_COUNT) % TILE_COUNT
                if (steps == 0) land(seat, e.tile, diceTotal) else moveSteps(seat, steps, diceTotal)
            }
            CardEffect.GoToJail -> goToJail(seat)
            CardEffect.JailPass -> s = s.copy(jailPasses = s.jailPasses + (seat to (s.jailPasses[seat] ?: 0) + 1))
            is CardEffect.MoveBack -> {
                val from = s.positions[seat] ?: 0
                val to = (from - e.steps + TILE_COUNT) % TILE_COUNT
                events += TokenMoved(seat, from, to, -e.steps, jump = false)
                s = s.copy(positions = s.positions + (seat to to))
                land(seat, to, diceTotal)
            }
            is CardEffect.CollectFromEach -> s.active.filter { it != seat }.forEach { other ->
                charge(other, e.amount, mapOf(seat to e.amount), MoneyReason.CARD, null)
            }
            is CardEffect.PayEach -> {
                val others = s.active.filter { it != seat }
                if (others.isNotEmpty()) charge(seat, e.amount * others.size, others.associateWith { e.amount }, MoneyReason.CARD, null)
            }
            is CardEffect.Repairs -> {
                val bill = CityBaazarEconomy.houses(s, seat) * e.perHouse + CityBaazarEconomy.hotels(s, seat) * e.perHotel
                if (bill > 0) charge(seat, bill, emptyMap(), MoneyReason.CARD, null)
            }
        }
    }

    private fun goToJail(seat: Int) {
        val from = s.positions[seat] ?: 0
        events += TokenMoved(seat, from, JAIL_TILE, 0, jump = true)
        events += SentToJail(seat)
        s = s.copy(
            positions = s.positions + (seat to JAIL_TILE),
            jailTries = s.jailTries + (seat to 0),
            rollAgain = false,
            doublesInRow = 0,
        )
        if (seat == s.currentSeat) nextTurn()
    }

    private fun leaveJail(seat: Int, how: String) {
        s = s.copy(jailTries = s.jailTries - seat)
        events += LeftJail(seat, how)
    }

    // ---------------- money ----------------

    private fun receive(seat: Int, amount: Int, reason: MoneyReason, tile: Int?) {
        s = s.copy(cash = s.cash + (seat to s.cashOf(seat) + amount))
        events += MoneyMoved(null, seat, amount, reason, tile)
    }

    /**
     * [seat] pays [total] ([payees] to players, the rest to the bank).
     * Short of money: the current seat raises it in the DEBT phase; anyone
     * else (a card asking every player) sells what the bot would, else goes
     * bankrupt. True when paid now.
     */
    private fun charge(seat: Int, total: Int, payees: Map<Int, Int>, reason: MoneyReason, tile: Int?): Boolean {
        if (s.cashOf(seat) >= total) {
            pay(seat, total, payees, reason, tile)
            return true
        }
        if (seat == s.currentSeat) {
            s = s.copy(phase = CityBaazarPhase.DEBT, debt = CityBaazarDebt(total, payees, reason, tile))
            events += DebtStarted(seat, total)
            return false
        }
        raiseByBot(seat, total)
        if (s.cashOf(seat) >= total) pay(seat, total, payees, reason, tile) else bankrupt(seat, payees)
        return true
    }

    private fun pay(seat: Int, total: Int, payees: Map<Int, Int>, reason: MoneyReason, tile: Int?) {
        var cash = s.cash + (seat to s.cashOf(seat) - total)
        payees.forEach { (to, amount) ->
            cash = cash + (to to (cash[to] ?: 0) + amount)
            events += MoneyMoved(seat, to, amount, reason, tile)
        }
        val toBank = total - payees.values.sum()
        if (toBank > 0) events += MoneyMoved(seat, null, toBank, reason, tile)
        s = s.copy(cash = cash)
    }

    /** Sells (as the bot would) until [seat] has [amount] or nothing is left. */
    private fun raiseByBot(seat: Int, amount: Int) {
        while (s.cashOf(seat) < amount) {
            val sale = CityBaazarBot.nextSale(s, seat) ?: return
            if (sale.second) sellBuilding(seat, sale.first) else sellTile(seat, sale.first)
        }
    }

    fun sellBuilding(seat: Int, tile: Int): Boolean {
        if (s.owners[tile] != seat || s.level(tile) == 0) return false
        val amount = CityBaazarEconomy.buildingResale(tile)
        val level = s.level(tile) - 1
        s = s.copy(levels = s.levels + (tile to level), cash = s.cash + (seat to s.cashOf(seat) + amount))
        events += BuildingSold(seat, tile, level, amount)
        events += MoneyMoved(null, seat, amount, MoneyReason.SELL, tile)
        return true
    }

    fun sellTile(seat: Int, tile: Int): Boolean {
        if (s.owners[tile] != seat || s.level(tile) > 0) return false
        val amount = CityBaazarEconomy.tileResale(tile)
        s = s.copy(owners = s.owners - tile, levels = s.levels - tile, cash = s.cash + (seat to s.cashOf(seat) + amount))
        events += TileSold(seat, tile, amount)
        events += MoneyMoved(null, seat, amount, MoneyReason.SELL, tile)
        return true
    }

    /** In DEBT: pays once the balance covers it, then the move carries on. */
    fun settleDebtIfPossible() {
        val debt = s.debt ?: return
        val seat = s.currentSeat
        if (s.cashOf(seat) < debt.total) return
        pay(seat, debt.total, debt.payees, debt.reason, debt.tile)
        s = s.copy(debt = null, phase = CityBaazarPhase.ROLL)
        val owed = s.pendingMove
        s = s.copy(pendingMove = null)
        if (owed != null) moveSteps(seat, owed, owed)
        settle()
    }

    /** Everything goes to the bank; what's left of the cash goes to [payees]; out of the match. */
    fun bankrupt(seat: Int, payees: Map<Int, Int>) {
        s.owners.filter { it.value == seat }.keys.forEach { tile ->
            while (s.level(tile) > 0) sellBuilding(seat, tile)
            sellTile(seat, tile)
        }
        var left = s.cashOf(seat)
        var cash = s.cash
        payees.forEach { (to, amount) ->
            val give = minOf(left, amount)
            if (give > 0) {
                cash = cash + (to to (cash[to] ?: 0) + give)
                events += MoneyMoved(seat, to, give, MoneyReason.BANKRUPTCY, null)
                left -= give
            }
        }
        val wasCurrent = seat == s.currentSeat
        s = s.copy(
            cash = cash + (seat to 0),
            seats = s.seats.filter { it.value != seat },
            bankruptOrder = s.bankruptOrder + seat,
            jailTries = s.jailTries - seat,
            skipNext = s.skipNext - seat,
            debt = if (wasCurrent) null else s.debt,
        )
        events += WentBankrupt(seat)
        if (s.active.size <= 1) return finish()
        if (wasCurrent) nextTurn()
    }

    // ---------------- decisions ----------------

    fun buy(seat: Int): Boolean {
        val tile = s.pendingTile ?: return false
        val price = CityBaazarBoard.tile(tile).price
        if (s.cashOf(seat) < price) return false
        s = s.copy(owners = s.owners + (tile to seat), cash = s.cash + (seat to s.cashOf(seat) - price))
        events += TileBought(seat, tile, price)
        events += MoneyMoved(seat, null, price, MoneyReason.BUY, tile)
        return true
    }

    fun build(seat: Int, tile: Int): Boolean {
        val cost = CityBaazarBoard.tile(tile).buildCost
        if (!CityBaazarEconomy.buildAllowed(s, seat, tile) || s.cashOf(seat) < cost) return false
        val level = s.level(tile) + 1
        s = s.copy(levels = s.levels + (tile to level), cash = s.cash + (seat to s.cashOf(seat) - cost))
        events += BuildingBuilt(seat, tile, level, cost)
        events += MoneyMoved(seat, null, cost, MoneyReason.BUILD, tile)
        return true
    }

    fun payJailFine(seat: Int): Boolean {
        if (s.cashOf(seat) < JAIL_FINE) return false
        pay(seat, JAIL_FINE, emptyMap(), MoneyReason.JAIL_FINE, JAIL_TILE)
        leaveJail(seat, "FINE")
        s = s.copy(phase = CityBaazarPhase.ROLL)
        return true
    }

    fun useJailPass(seat: Int): Boolean {
        val passes = s.jailPasses[seat] ?: 0
        if (passes == 0) return false
        s = s.copy(jailPasses = s.jailPasses + (seat to passes - 1), phase = CityBaazarPhase.ROLL)
        leaveJail(seat, "PASS")
        return true
    }

    /** A BUY / BUILD decision is done: carry on with the move. */
    fun decided() {
        s = s.copy(phase = CityBaazarPhase.ROLL, pendingTile = null)
        settle()
    }

    // ---------------- turn order ----------------

    private fun settle() {
        if (ended || s.phase in WAITING) return
        val seat = s.currentSeat
        if (s.rollAgain && !s.inJail(seat) && seat in s.active) {
            s = s.copy(phase = CityBaazarPhase.ROLL, pendingTile = null)
        } else {
            nextTurn()
        }
    }

    fun nextTurn() {
        ended = true
        val active = s.active.toSet()
        if (active.size <= 1) return finish()
        var seat = s.currentSeat
        var round = s.round
        var skip = s.skipNext
        while (true) {
            val next = (1..s.seatCount).map { (seat + it - 1) % s.seatCount + 1 }.first { it in active }
            if (next <= seat) round++
            seat = next
            if (seat in skip) {
                skip = skip - seat
                events += TurnSkipped(seat)
            } else {
                break
            }
        }
        s = s.copy(
            currentSeat = seat,
            round = round,
            skipNext = skip,
            phase = if (s.inJail(seat)) CityBaazarPhase.JAIL_CHOICE else CityBaazarPhase.ROLL,
            rollAgain = false,
            doublesInRow = 0,
            pendingTile = null,
            pendingMove = null,
            debt = null,
        )
        if (s.cbRules.house.roundLimit && round > ROUND_LIMIT) return finish()
        events += TurnPassed(seat)
    }

    private fun finish() {
        ended = true
        s = s.copy(phase = CityBaazarPhase.FINISHED, pendingTile = null, debt = null)
        events += MatchEnded(CityBaazarEngine.ranking(s))
    }

    /** A seat leaves for good: everything goes back to the bank. */
    fun remove(seat: Int) {
        bankrupt(seat, emptyMap())
    }

    fun state(): CityBaazarState = s

    private companion object {
        val WAITING = setOf(CityBaazarPhase.BUY, CityBaazarPhase.BUILD, CityBaazarPhase.DEBT, CityBaazarPhase.FINISHED)
    }
}
