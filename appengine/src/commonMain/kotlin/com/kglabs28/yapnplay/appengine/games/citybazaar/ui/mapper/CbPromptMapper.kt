package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper

import kotlinx.datetime.Instant
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUiMapper
import com.kglabs28.yapnplay.appengine.games.base.match.MatchEndVote
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

/** The user's decisions: buy, build, jail, debt, auction, keep-playing vote. */
object CbPromptMapper {
    /** The continue-or-end vote as the user sees it: buttons if they may vote and haven't yet. */
    fun extensionVote(match: CityBazaarMatch, vote: CityBazaarExtensionVoteInfo, countdown: String): CbModal.ExtensionVote {
        val seat = match.me?.seat
        val eligible = seat != null && seat in vote.voters
        val votedYes = seat != null && seat in vote.yes
        val votedNo = seat != null && seat in vote.no
        return CbModal.ExtensionVote(
            body = CityBazaarStrings.cbExtendBody(extraLabel(vote)),
            tally = CityBazaarStrings.cbExtendTally(vote.yes.size, vote.voters.size),
            status = when {
                !eligible -> CityBazaarStrings.cbExtendWatching
                votedYes || votedNo -> CityBazaarStrings.cbExtendYouVoted(votedYes)
                else -> null
            },
            canVote = eligible && !votedYes && !votedNo,
            countdown = countdown,
        )
    }

    /** The end-game vote as the user sees it: buttons if they may vote and haven't yet. */
    fun endVote(match: CityBazaarMatch, vote: MatchEndVote, now: Instant): CbModal.EndVote {
        val ui = EndVoteUiMapper.of(vote, match.me?.seat, match.player(vote.proposerSeat)?.name.orEmpty(), CityBazaarStrings.cbEndRanking, now)
        return CbModal.EndVote(body = ui.body, tally = ui.tally, status = ui.status, canVote = ui.canVote, countdown = ui.countdown)
    }

    /** "30 rounds" or "15 minutes", by which limit was hit. */
    fun extraLabel(vote: CityBazaarExtensionVoteInfo?): String = when {
        vote?.extraMinutes != null -> CityBazaarStrings.cbExtraMinutes(vote.extraMinutes)
        else -> CityBazaarStrings.cbExtraRounds(vote?.extraRounds ?: DEFAULT_EXTRA_ROUNDS)
    }

    // ---------------- decisions ----------------

    fun sheet(balance: Int, label: String, amount: Int, gain: Boolean): List<CbSheetRowUi> = listOf(
        CbSheetRowUi(CityBazaarStrings.cbCurrentBalance, CbFormat.rupees(balance), CbRowKind.NORMAL),
        CbSheetRowUi(label, if (gain) CbFormat.plus(amount) else CbFormat.minus(amount), if (gain) CbRowKind.PLUS else CbRowKind.MINUS),
        CbSheetRowUi(CityBazaarStrings.cbRemaining, CbFormat.rupees(if (gain) balance + amount else balance - amount), CbRowKind.TOTAL),
    )

    fun buy(me: CityBazaarPlayer, tile: Int, countdown: String): CbModal.Buy {
        val spec = CityBazaarBoard.tile(tile)
        return CbModal.Buy(
            tile = tile,
            name = spec.name,
            group = spec.group,
            kind = spec.kind,
            headline = if (spec.kind == TileKind.CITY) CityBazaarStrings.cbBuyHeadline(CbFormat.rupees(spec.rents[0]), CbFormat.rupees(spec.rents[1])) else CityBazaarStrings.cbPairNote(CbPropertyMapper.partnerName(tile)),
            rentLand = if (spec.kind == TileKind.CITY) CbFormat.rupees(spec.rents[0]) else null,
            rentHouse = if (spec.kind == TileKind.CITY) CbFormat.rupees(spec.rents[1]) else null,
            price = CbFormat.rupees(spec.price),
            // One highlighted tier: the rent it earns right after buying (land; a company alone).
            rates = CbPropertyMapper.rates(tile, 0),
            costs = costRows(tile),
            cash = CbFormat.rupees(me.cash),
            cost = CbFormat.minus(spec.price),
            remaining = CbFormat.rupees(me.cash - spec.price),
            canAfford = me.cash >= spec.price,
            countdown = countdown,
        )
    }

    fun build(match: CityBazaarMatch, me: CityBazaarPlayer, tile: Int, countdown: String): CbModal.Build {
        val spec = CityBazaarBoard.tile(tile)
        val level = match.levels[tile] ?: 0
        val next = (level + 1).coerceAtMost(HOTEL_LEVEL)
        return CbModal.Build(
            tile = tile,
            name = spec.name,
            group = spec.group,
            price = CbFormat.rupees(spec.price),
            rates = CbPropertyMapper.rates(tile, next),
            costs = costRows(tile),
            what = CityBazaarStrings.cbFeedLevel(next),
            cost = CbFormat.minus(spec.buildCost),
            newRent = CityBazaarStrings.cbNewRent(CbFormat.rupees(spec.rents.getOrElse(next) { 0 })),
            cash = CbFormat.rupees(me.cash),
            remaining = CbFormat.rupees(me.cash - spec.buildCost),
            canAfford = me.cash >= spec.buildCost,
            countdown = countdown,
        )
    }

    /** Property price, and for a city the house and hotel cost (as on its card). */
    fun costRows(tile: Int): List<CbSheetRowUi> {
        val spec = CityBazaarBoard.tile(tile)
        return buildList {
            add(CbSheetRowUi(CityBazaarStrings.cbPrice, CbFormat.rupees(spec.price), CbRowKind.NORMAL))
            if (spec.kind == TileKind.CITY) {
                add(CbSheetRowUi(CityBazaarStrings.cbHouseCost, CbFormat.rupees(spec.buildCost), CbRowKind.NORMAL))
                add(CbSheetRowUi(CityBazaarStrings.cbHotelCost, CbFormat.rupees(spec.buildCost), CbRowKind.NORMAL))
            }
        }
    }

    fun jailRoll(match: CityBazaarMatch, me: CityBazaarPlayer, countdown: String): CbModal.JailRoll =
        CbModal.JailRoll(CityBazaarStrings.cbJailRollBody(match.jailTriesLeft), me.cash >= JAIL_FINE, countdown)

    fun jailChoice(me: CityBazaarPlayer, countdown: String): CbModal.JailChoice = CbModal.JailChoice(me.cash >= JAIL_FINE, countdown)

    /**
     * What the user can sell to pay a debt, one step per city in reverse of
     * building: a built city offers its top building to the bank (a hotel
     * goes back to 3 houses, then house by house); a bare city offers itself
     * (up for auction under the house rule, else to the bank). The user picks
     * any city's step.
     */
    fun assets(match: CityBazaarMatch, seat: Int): List<CbAssetUi> =
        match.owners.filter { it.value == seat }.keys.sorted().map { tile ->
            val spec = CityBazaarBoard.tile(tile)
            val level = match.levels[tile] ?: 0
            if (level > 0) {
                CbAssetUi(BUILDING + tile, tile, spec.name, spec.group, spec.kind, if (level >= HOTEL_LEVEL) CityBazaarStrings.cbSellHotel else CityBazaarStrings.cbSellHouse, CbFormat.plus(spec.buildCost / RESALE_DIVISOR))
            } else {
                val label = if (match.debtAuction) CityBazaarStrings.cbAuctionProperty else CityBazaarStrings.cbSellProperty
                CbAssetUi(TILE + tile, tile, spec.name, spec.group, spec.kind, label, CbFormat.plus(spec.price / RESALE_DIVISOR))
            }
        }

    fun assetValue(id: String): Int {
        val spec = CityBazaarBoard.tile(assetTile(id))
        return if (isBuilding(id)) spec.buildCost / RESALE_DIVISOR else spec.price / RESALE_DIVISOR
    }

    fun isBuilding(id: String): Boolean = id.startsWith(BUILDING)

    fun assetTile(id: String): Int = id.drop(1).toInt()

    fun debt(match: CityBazaarMatch, me: CityBazaarPlayer, selected: Set<String>, countdown: String): CbModal.Debt? {
        val owed = match.debt?.amount ?: return null
        val deficit = (owed - me.cash).coerceAtLeast(0)
        val picked = selected.sumOf { assetValue(it) }
        return CbModal.Debt(
            body = CityBazaarStrings.cbDebtBody(CbFormat.rupees(deficit)),
            assets = assets(match, me.seat),
            selected = selected,
            tracker = CityBazaarStrings.cbSelectedValue(CbFormat.rupees(picked), CbFormat.rupees(deficit)),
            enough = picked >= deficit,
            canAuction = selected.size == 1 && !isBuilding(selected.first()),
            countdown = countdown,
        )
    }

    /**
     * The auction prompt for [viewer], like the buy prompt: the city's rents
     * and costs, its price crossed out before the bid, owner, highest bidder
     * and balance after bidding. Only for someone who can bid higher: null
     * for the owner, the highest bidder and anyone who can't afford the next bid.
     */
    fun auction(match: CityBazaarMatch, auction: CityBazaarAuctionInfo, viewer: CityBazaarPlayer, countdown: String): CbModal.Auction? {
        if (viewer.seat == auction.sellerSeat || viewer.seat == auction.leaderSeat || viewer.cash < auction.nextBid) return null
        val spec = CityBazaarBoard.tile(auction.tile)
        val seller = match.player(auction.sellerSeat)?.name.orEmpty()
        val leader = auction.leaderSeat?.let { match.player(it)?.name }
        val bid = CbFormat.rupees(auction.bid)
        return CbModal.Auction(
            tile = auction.tile,
            name = spec.name,
            group = spec.group,
            kind = spec.kind,
            body = leader?.let { CityBazaarStrings.cbAuctionOutbid(it, bid) } ?: CityBazaarStrings.cbAuctionBody(seller, spec.name, bid),
            price = bid,
            oldPrice = CbFormat.rupees(spec.price),
            // Auctioned cities are bare: the rent it earns right after buying is lit.
            rates = CbPropertyMapper.rates(auction.tile, 0),
            costs = costRows(auction.tile),
            owner = CityBazaarStrings.cbOwnerLine(seller),
            leader = leader?.let { CityBazaarStrings.cbHighestBid(it, bid) } ?: CityBazaarStrings.cbNoBidsYet,
            cash = CbFormat.rupees(viewer.cash),
            cost = CbFormat.minus(auction.nextBid),
            remaining = CbFormat.rupees(viewer.cash - auction.nextBid),
            bidLabel = CityBazaarStrings.cbBid(CbFormat.rupees(auction.nextBid)),
            countdown = countdown,
        )
    }

    private const val DEFAULT_EXTRA_ROUNDS = 30
    private const val BUILDING = "b"
    private const val TILE = "t"
}
