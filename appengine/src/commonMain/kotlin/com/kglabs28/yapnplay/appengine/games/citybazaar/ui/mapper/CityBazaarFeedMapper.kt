package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper

import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedSpanKind
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedSpan
import com.kglabs28.yapnplay.appengine.core.ui.Strings
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
    fun deckLabel(cardId: String): String? = CityBazaarCards.find(cardId)?.let { if (it.deck == CardDeck.LUCK) Strings.cbLuckLabel else Strings.cbChestLabel }

    /** The line for [event] with its highlights (players, money, places, card piles), or null when it's shown another way. */
    fun line(event: CityBazaarEvent, m: CityBazaarMatch, lastDeck: String?): TableFeedLineUi? =
        plainLine(event, m, lastDeck)?.let { it.copy(spans = highlights(it.text, m)) }

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
        listOf(Strings.cbLuckLabel, Strings.cbChestLabel).forEach { mark(it, TableFeedSpanKind.CARD) }
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
        fun money(amount: Int): String = CityBazaarUiMapper.rupees(amount)
        fun tone(gainer: Int?, loser: Int?): TableFeedTone = when {
            me != null && gainer == me -> TableFeedTone.GAIN
            me != null && loser == me -> TableFeedTone.LOSS
            else -> TableFeedTone.INFO
        }
        fun of(text: String, tone: TableFeedTone = TableFeedTone.INFO, quiet: Boolean = false) = TableFeedLineUi(0, text, tone, quiet)
        return when (event) {
            is CityBazaarEvent.Rolled -> of(Strings.cbFeedRolled(name(event.seat), event.die1, event.die2), quiet = true)
            is CityBazaarEvent.Money -> {
                val from = event.from
                val to = event.to
                val amount = money(event.amount)
                val deck = lastDeck ?: Strings.cbFeedCardDefault
                val text = when (event.reason) {
                    MoneyReason.BUY -> Strings.cbFeedBought(name(from), tile(event.tile), amount)
                    MoneyReason.SELL -> Strings.cbFeedSold(name(to), tile(event.tile), amount)
                    MoneyReason.RENT -> Strings.cbFeedRent(name(from), name(to), amount, tile(event.tile))
                    MoneyReason.TAX -> Strings.cbFeedTax(name(from), amount, tile(event.tile))
                    MoneyReason.CLUB -> if (from == null) Strings.cbFeedClubGot(name(to), amount) else Strings.cbFeedClubPaid(name(from), amount)
                    MoneyReason.START -> Strings.cbFeedStart(name(to), amount)
                    MoneyReason.JAIL_FINE -> Strings.cbFeedJailFine(name(from), amount)
                    MoneyReason.LATE_ROLL -> Strings.cbFeedLate(name(from), amount)
                    MoneyReason.CARD -> when {
                        from == null -> Strings.cbFeedCardWon(name(to), amount, deck)
                        to == null -> Strings.cbFeedCardPaid(name(from), amount, deck)
                        else -> Strings.cbFeedPaidTo(name(from), name(to), amount)
                    }
                    // Told by Built, AuctionEnded and Bankrupt.
                    MoneyReason.BUILD, MoneyReason.AUCTION, MoneyReason.BANKRUPTCY -> null
                } ?: return null
                of(text, tone(gainer = to, loser = from))
            }
            is CityBazaarEvent.Card -> deckLabel(event.cardId)?.let { deck ->
                of(Strings.cbFeedDrew(name(event.seat), deck, CityBazaarCards.find(event.cardId)?.text.orEmpty()))
            }
            is CityBazaarEvent.Built -> {
                val spec = CityBazaarBoard.tile(event.tile)
                of(Strings.cbFeedBuilt(name(event.seat), Strings.cbFeedLevel(event.level), spec.name, money(spec.buildCost)), tone(gainer = null, loser = event.seat))
            }
            is CityBazaarEvent.CantAfford -> of(Strings.cbFeedCantAfford(name(event.seat), tile(event.tile), money(CityBazaarBoard.tile(event.tile).price)))
            is CityBazaarEvent.Jailed -> of(Strings.cbFeedJailed(name(event.seat)), tone(gainer = null, loser = event.seat))
            is CityBazaarEvent.Freed -> of(Strings.cbFeedFreed(name(event.seat)))
            is CityBazaarEvent.HalfRent -> of(Strings.cbFeedHalfRent(name(event.seat)))
            is CityBazaarEvent.AuctionStarted -> of(Strings.cbFeedAuctionStarted(name(event.seller), tile(event.tile), money(event.opening)))
            is CityBazaarEvent.BidPlaced -> of(Strings.cbFeedBid(name(event.seat), money(event.amount), tile(event.tile)), quiet = true)
            is CityBazaarEvent.AuctionEnded -> {
                val text = event.winner?.let { Strings.cbAuctionWon(name(it), tile(event.tile), money(event.amount)) }
                    ?: Strings.cbAuctionBank(tile(event.tile), money(event.amount))
                of(text, tone(gainer = event.seller, loser = event.winner))
            }
            is CityBazaarEvent.Bankrupt -> of(Strings.cbFeedBankrupt(name(event.seat)), tone(gainer = null, loser = event.seat))
            is CityBazaarEvent.Skipped -> of(Strings.cbFeedSkipped(name(event.seat)))
            is CityBazaarEvent.Rested -> of(Strings.cbFeedRested(name(event.seat)))
            is CityBazaarEvent.ExtensionVoted -> of(
                if (event.continued) Strings.cbFeedExtended(CityBazaarUiMapper.extraLabel(m.extensionVote), event.yes, event.voters)
                else Strings.cbFeedNotExtended(event.yes, event.voters),
            )
            is CityBazaarEvent.Moved, is CityBazaarEvent.Bought, is CityBazaarEvent.Sold, is CityBazaarEvent.TurnPassed -> null
        }
    }
}
