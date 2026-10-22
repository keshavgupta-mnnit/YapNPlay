package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedSpanKind
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedSpan
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarEvent
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatch
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarBoard
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarCards
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.MoneyReason
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedLineUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedTone

/**
 * Every City Bazaar event as one line for the snackbar and the history:
 * who bought, built, paid whom and why, cards, jail, auctions. The tone is
 * from the user's side (money in green, out red, others' news cyan).
 */
object CityBazaarFeedMapper {

    /** "LUCK" / "SHAGUN CHEST" for a drawn card. */
    fun deckLabel(cardId: String): String? = CityBazaarCards.find(cardId)?.let { if (it.deck == CardDeck.LUCK) CityBazaarStrings.cbLuckLabel else CityBazaarStrings.cbChestLabel }

    /** The line for [event] with its highlights (players, money, places, card piles), or null when it's shown another way. */
    fun line(event: CityBazaarEvent, m: CityBazaarMatch, lastDeck: String?): TableFeedLineUi? =
        plainLine(event, m, lastDeck)?.let { it.copy(spans = highlights(it.text, m)) }

    /** [line] reworded to [text], its highlights found afresh in the new words. */
    fun reworded(line: TableFeedLineUi, text: String, m: CityBazaarMatch): TableFeedLineUi =
        line.copy(text = text, spans = highlights(text, m))

    /** Players (by colour), amounts, tile names and card piles in [text], left to right, never overlapping. */
    private fun highlights(text: String, m: CityBazaarMatch): List<TableFeedSpan> {
        val found = mutableListOf<TableFeedSpan>()
        fun mark(word: String, kind: TableFeedSpanKind, colour: Int = -1) {
            if (word.isBlank()) return
            var from = 0
            while (true) {
                val at = text.indexOf(word, from)
                if (at < 0) return
                val end = at + word.length
                if (found.none { at < it.end && end > it.start }) found += TableFeedSpan(at, end, kind, colour)
                from = end
            }
        }
        m.players.sortedByDescending { it.name.length }.forEach { mark(it.name, TableFeedSpanKind.PLAYER, it.colour.ordinal) }
        listOf(CityBazaarStrings.cbLuckLabel, CityBazaarStrings.cbChestLabel).forEach { mark(it, TableFeedSpanKind.CARD) }
        CityBazaarBoard.tiles.map { it.name }.filter { it.length > MIN_PLACE }.sortedByDescending { it.length }.forEach { mark(it, TableFeedSpanKind.PLACE) }
        MONEY.findAll(text).forEach { mark(it.value, TableFeedSpanKind.MONEY) }
        return found.sortedBy { it.start }
    }

    private const val MIN_PLACE = 2
    private val MONEY = Regex("₹[0-9,]+")

    private fun plainLine(event: CityBazaarEvent, m: CityBazaarMatch, lastDeck: String?): TableFeedLineUi? {
        val me = m.me?.seat
        fun name(seat: Int?): String = seat?.let { m.player(it)?.name }.orEmpty()
        fun tile(index: Int?): String = index?.let { CityBazaarBoard.tile(it).name }.orEmpty()
        fun money(amount: Int): String = CbFormat.rupees(amount)
        fun tone(gainer: Int?, loser: Int?): TableFeedTone = when {
            me != null && gainer == me -> TableFeedTone.GAIN
            me != null && loser == me -> TableFeedTone.LOSS
            else -> TableFeedTone.INFO
        }
        fun of(text: String, tone: TableFeedTone = TableFeedTone.INFO, quiet: Boolean = false) = TableFeedLineUi(0, text, tone, quiet)
        return when (event) {
            is CityBazaarEvent.Rolled -> of(CityBazaarStrings.cbFeedRolled(name(event.seat), event.die1, event.die2), quiet = true)
            is CityBazaarEvent.Money -> {
                val from = event.from
                val to = event.to
                val amount = money(event.amount)
                val deck = lastDeck ?: CityBazaarStrings.cbFeedCardDefault
                val text = when (event.reason) {
                    MoneyReason.BUY -> CityBazaarStrings.cbFeedBought(name(from), tile(event.tile), amount)
                    MoneyReason.SELL -> CityBazaarStrings.cbFeedSold(name(to), tile(event.tile), amount)
                    MoneyReason.RENT -> CityBazaarStrings.cbFeedRent(name(from), name(to), amount, tile(event.tile))
                    MoneyReason.TAX -> CityBazaarStrings.cbFeedTax(name(from), amount, tile(event.tile))
                    MoneyReason.CLUB -> when {
                        from != null && to != null -> CityBazaarStrings.cbFeedClubPaidTo(name(from), name(to), amount)
                        from == null -> CityBazaarStrings.cbFeedClubGot(name(to), amount)
                        else -> CityBazaarStrings.cbFeedClubPaid(name(from), amount)
                    }
                    MoneyReason.REST -> CityBazaarStrings.cbFeedRestPaidTo(name(from), name(to), amount)
                    MoneyReason.START -> CityBazaarStrings.cbFeedStart(name(to), amount)
                    MoneyReason.JAIL_FINE -> CityBazaarStrings.cbFeedJailFine(name(from), amount)
                    MoneyReason.LATE_ROLL -> CityBazaarStrings.cbFeedLate(name(from), amount)
                    MoneyReason.CARD -> when {
                        from == null -> CityBazaarStrings.cbFeedCardWon(name(to), amount, deck)
                        to == null -> CityBazaarStrings.cbFeedCardPaid(name(from), amount, deck)
                        else -> CityBazaarStrings.cbFeedPaidTo(name(from), name(to), amount)
                    }
                    // Told by Built, AuctionEnded and Bankrupt.
                    MoneyReason.BUILD, MoneyReason.AUCTION, MoneyReason.BANKRUPTCY -> null
                } ?: return null
                of(text, tone(gainer = to, loser = from))
            }
            is CityBazaarEvent.Card -> deckLabel(event.cardId)?.let { deck ->
                of(CityBazaarStrings.cbFeedDrew(name(event.seat), deck, CityBazaarCards.find(event.cardId)?.text.orEmpty()))
            }
            is CityBazaarEvent.Built -> {
                val spec = CityBazaarBoard.tile(event.tile)
                of(CityBazaarStrings.cbFeedBuilt(name(event.seat), CityBazaarStrings.cbFeedLevel(event.level), spec.name, money(spec.buildCost)), tone(gainer = null, loser = event.seat))
            }
            is CityBazaarEvent.CantAfford -> of(CityBazaarStrings.cbFeedCantAfford(name(event.seat), tile(event.tile), money(CityBazaarBoard.tile(event.tile).price)))
            is CityBazaarEvent.Jailed -> of(CityBazaarStrings.cbFeedJailed(name(event.seat)), tone(gainer = null, loser = event.seat))
            is CityBazaarEvent.Freed -> of(CityBazaarStrings.cbFeedFreed(name(event.seat)))
            is CityBazaarEvent.HalfRent -> of(CityBazaarStrings.cbFeedHalfRent(name(event.seat)))
            is CityBazaarEvent.AuctionStarted -> of(CityBazaarStrings.cbFeedAuctionStarted(name(event.seller), tile(event.tile), money(event.opening)))
            is CityBazaarEvent.BidPlaced -> of(CityBazaarStrings.cbFeedBid(name(event.seat), money(event.amount), tile(event.tile)), quiet = true)
            is CityBazaarEvent.AuctionEnded -> {
                val text = event.winner?.let { CityBazaarStrings.cbAuctionWon(name(it), tile(event.tile), money(event.amount)) }
                    ?: CityBazaarStrings.cbAuctionBank(tile(event.tile), money(event.amount))
                of(text, tone(gainer = event.seller, loser = event.winner))
            }
            is CityBazaarEvent.Bankrupt -> of(CityBazaarStrings.cbFeedBankrupt(name(event.seat)), tone(gainer = null, loser = event.seat))
            is CityBazaarEvent.Skipped -> of(CityBazaarStrings.cbFeedSkipped(name(event.seat)))
            is CityBazaarEvent.Rested -> of(CityBazaarStrings.cbFeedRested(name(event.seat)))
            is CityBazaarEvent.ExtensionVoted -> of(
                if (event.continued) CityBazaarStrings.cbFeedExtended(CbPromptMapper.extraLabel(m.extensionVote), event.yes, event.voters)
                else CityBazaarStrings.cbFeedNotExtended(event.yes, event.voters),
            )
            is CityBazaarEvent.EndVoted -> of(if (event.ended) CityBazaarStrings.cbFeedEndVoted(event.yes, event.voters) else CityBazaarStrings.cbFeedEndRejected(event.yes, event.voters))
            is CityBazaarEvent.Moved, is CityBazaarEvent.Bought, is CityBazaarEvent.Sold, is CityBazaarEvent.TurnPassed -> null
        }
    }
}
