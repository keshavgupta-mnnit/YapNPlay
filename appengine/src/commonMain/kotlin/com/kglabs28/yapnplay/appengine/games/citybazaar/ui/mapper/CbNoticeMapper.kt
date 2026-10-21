package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbNoticeArt
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.MoneyReason
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarEvent
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarExtensionVoteInfo
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTransactionUi
import com.kglabs28.yapnplay.appengine.ui.components.profile.AvatarPresets
import com.kglabs28.yapnplay.appengine.ui.components.table.AvatarRing
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarAuctionInfo
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarColour
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatch
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarPlayer
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarPresence
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarTurnPhase
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardEffect
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarBoard
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarCards
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityGroup
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.HOTEL_LEVEL
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.JAIL_FINE
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.RESALE_DIVISOR
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.RupeeFormat
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.SET_FOR_DOUBLE
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbAssetUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbDeckRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbModal
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbOwnedRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPanel
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPlayerCardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPropertyCardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbRateRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbResultRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbRowKind
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbSheetRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbStackUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTileUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbToastUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTokenUi
import kotlin.time.Duration

/** Popups and toasts: the user's money and events. */
object CbNoticeMapper {
    /** Someone else's payment, for everyone: who paid whom (or the bank), how much, and why. */
    fun paymentToast(payer: String, payee: String?, reason: String, amount: Int): CbToastUi = CbToastUi(
        0,
        CityBazaarStrings.cbPaid(payer),
        listOf(
            CbSheetRowUi(if (payee != null) CityBazaarStrings.cbPaidTo(payer, payee) else CityBazaarStrings.cbPaidBank(payer), CbFormat.minus(amount), CbRowKind.MINUS),
            CbSheetRowUi(reason, "", CbRowKind.NORMAL),
        ),
        gain = false,
    )

    /** The user's popup for something without money (a card, jail, Rest House, out of jail, bankrupt); null otherwise. */
    fun notice(match: CityBazaarMatch, event: CityBazaarEvent): CbTransactionUi? {
        fun of(seat: Int, title: String, line: String, art: CbNoticeArt): CbTransactionUi {
            val player = match.player(seat)
            return CbTransactionUi(
                id = 0, title = title, line = line, status = "", fromAvatarId = player?.avatarId, toAvatarId = null, amount = null,
                cash = "", changeLabel = "", change = "", remaining = "", gain = false, art = art,
            )
        }
        return when (event) {
            is CityBazaarEvent.Card -> CityBazaarCards.find(event.cardId)?.let { spec ->
                val luck = spec.deck == CardDeck.LUCK
                of(event.seat, if (luck) CityBazaarStrings.cbLuckLabel else CityBazaarStrings.cbChestLabel, CityBazaarStrings.cbYouDrew(spec.text), if (luck) CbNoticeArt.LUCK else CbNoticeArt.CHEST)
            }
            is CityBazaarEvent.Jailed -> of(event.seat, CityBazaarStrings.cbTitleJail, CityBazaarStrings.cbYouJail, CbNoticeArt.JAIL)
            is CityBazaarEvent.Rested -> of(event.seat, CityBazaarStrings.cbTitleRest, CityBazaarStrings.cbYouRest, CbNoticeArt.REST)
            is CityBazaarEvent.Freed -> of(event.seat, CityBazaarStrings.cbTitleFreed, CityBazaarStrings.cbYouFreed, CbNoticeArt.FREED)
            is CityBazaarEvent.Bankrupt -> of(event.seat, CityBazaarStrings.cbTitleBankrupt, CityBazaarStrings.cbYouBankrupt, CbNoticeArt.BANKRUPT)
            else -> null
        }
    }

    /**
     * A money event as the transaction popup. [cashBefore] is the shown
     * balance of [seat] (the user if it's theirs, else the payer, else the
     * receiver) before the move; null [seat]: no balance strip values.
     */
    fun transaction(match: CityBazaarMatch, event: CityBazaarEvent.Money, seat: Int?, cashBefore: Int, line: String?, deck: String?, cardText: String?): CbTransactionUi? {
        val tile = event.tile?.let { CityBazaarBoard.tile(it).name }.orEmpty()
        val payer = event.from?.let { match.player(it) }
        val payee = event.to?.let { match.player(it) }
        val amount = CbFormat.rupees(event.amount)
        val me = match.me?.seat
        val other = (if (event.from == me) payee else payer)?.name.orEmpty()
        val paying = event.from == me
        val title = when (event.reason) {
            MoneyReason.RENT -> CityBazaarStrings.cbYouRentTitle(tile)
            MoneyReason.TAX -> tile.uppercase()
            MoneyReason.CLUB -> CityBazaarStrings.cbTitleClub
            MoneyReason.REST -> CityBazaarStrings.cbTitleRestHouse
            MoneyReason.START -> CityBazaarStrings.cbTitleStart
            MoneyReason.JAIL_FINE -> CityBazaarStrings.cbTitleJailFine
            MoneyReason.LATE_ROLL -> CityBazaarStrings.cbTitleLate
            MoneyReason.CARD -> deck ?: CityBazaarStrings.cbTxPayment
            MoneyReason.BUY -> CityBazaarStrings.cbTitleBought(tile)
            MoneyReason.BUILD -> CityBazaarStrings.cbTitleBuilt(tile)
            MoneyReason.SELL -> CityBazaarStrings.cbTitleSold(tile)
            MoneyReason.AUCTION -> CityBazaarStrings.cbTitleAuction(tile)
            MoneyReason.BANKRUPTCY -> return null
        }
        val what = cardText ?: deck.orEmpty()
        val youLine = when (event.reason) {
            MoneyReason.RENT -> if (paying) CityBazaarStrings.cbYouPaidRent(other, amount) else CityBazaarStrings.cbYouGotRent(other, amount)
            MoneyReason.START -> CityBazaarStrings.cbYouStart(amount)
            MoneyReason.TAX -> CityBazaarStrings.cbYouTax(amount, tile)
            MoneyReason.JAIL_FINE -> CityBazaarStrings.cbYouJailFine(amount)
            MoneyReason.LATE_ROLL -> CityBazaarStrings.cbYouLate(amount)
            MoneyReason.CLUB -> when {
                payer != null && payee != null -> if (paying) CityBazaarStrings.cbYouClubPaidTo(other, amount) else CityBazaarStrings.cbYouClubGotFrom(other, amount)
                paying -> CityBazaarStrings.cbYouClubPaid(amount)
                else -> CityBazaarStrings.cbYouClubGot(amount)
            }
            MoneyReason.REST -> if (paying) CityBazaarStrings.cbYouRestPaidTo(other, amount) else CityBazaarStrings.cbYouRestGotFrom(other, amount)
            MoneyReason.CARD -> when {
                payer == null -> CityBazaarStrings.cbYouWon(amount, what)
                payee == null -> CityBazaarStrings.cbYouPaidFor(amount, what)
                paying -> CityBazaarStrings.cbYouPaidTo(other, amount)
                else -> CityBazaarStrings.cbYouGotFrom(other, amount)
            }
            MoneyReason.BUY -> CityBazaarStrings.cbYouBought(tile, amount)
            MoneyReason.BUILD -> CityBazaarStrings.cbYouBuilt(tile, amount)
            MoneyReason.SELL -> CityBazaarStrings.cbYouSold(tile, amount)
            MoneyReason.AUCTION -> if (paying) CityBazaarStrings.cbYouAuctionPaid(amount) else CityBazaarStrings.cbYouAuctionGot(amount)
            MoneyReason.BANKRUPTCY -> return null
        }
        val gain = seat != null && event.to == seat
        return CbTransactionUi(
            id = 0,
            title = title,
            line = youLine,
            status = when {
                me != null && event.from == me -> CityBazaarStrings.cbTxDeducting
                me != null && event.to == me -> CityBazaarStrings.cbTxCrediting
                else -> CityBazaarStrings.cbTxTransferring
            },
            fromAvatarId = payer?.avatarId,
            toAvatarId = payee?.avatarId,
            amount = amount,
            cash = CbFormat.rupees(cashBefore),
            changeLabel = when {
                !gain && event.reason == MoneyReason.RENT -> CityBazaarStrings.cbRentPaidLabel
                gain -> CityBazaarStrings.cbTxReceivedLabel
                else -> CityBazaarStrings.cbTxPaidLabel
            },
            change = if (gain) CbFormat.plus(event.amount) else CbFormat.minus(event.amount),
            remaining = CbFormat.rupees(if (gain) cashBefore + event.amount else cashBefore - event.amount),
            gain = gain,
        )
    }

    fun moneyToast(title: String, balance: Int, label: String, amount: Int, gain: Boolean): CbToastUi =
        CbToastUi(0, title, CbPromptMapper.sheet(balance, label, amount, gain), gain)

    fun noteToast(title: String, line: String, gain: Boolean = true): CbToastUi =
        CbToastUi(0, title, listOf(CbSheetRowUi(line, "", CbRowKind.NORMAL)), gain)
}
