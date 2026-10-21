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

/** Properties and side panels: rent tiers, property cards, player / bank panels, card tables. */
object CbPropertyMapper {
    // ---------------- cards ----------------

    /** The tiers of [tile] with [current] highlighted (cities: land, 1–3 houses, hotel; others: one fixed rent). */
    fun rates(tile: Int, current: Int?): List<CbRateRowUi> {
        val spec = CityBazaarBoard.tile(tile)
        return when (spec.kind) {
            TileKind.CITY -> listOf(CbRateRowUi(CityBazaarStrings.cbRentLand, CbFormat.rupees(spec.rents[0]), current == 0)) +
                (1 until HOTEL_LEVEL).map { CbRateRowUi(CityBazaarStrings.cbRentHouses(it), CbFormat.rupees(spec.rents[it]), current == it) } +
                CbRateRowUi(CityBazaarStrings.cbRentHotel, CbFormat.rupees(spec.rents[HOTEL_LEVEL]), current == HOTEL_LEVEL)
            TileKind.COMPANY -> listOf(
                CbRateRowUi(CityBazaarStrings.cbRentLand, CbFormat.rupees(spec.rents[0]), current == 0),
                CbRateRowUi(CityBazaarStrings.cbRentWithPartner(partnerName(tile)), CbFormat.rupees(spec.rents[1]), current == 1),
            )
            TileKind.TAX -> listOf(
                CbRateRowUi(
                    CityBazaarStrings.cbTax,
                    if (spec.taxPerProperty > 0) CityBazaarStrings.cbPerProperty(RupeeFormat.digits(spec.taxPerProperty)) else CityBazaarStrings.cbPerBuilding(RupeeFormat.digits(spec.taxPerBuilding)),
                    true,
                ),
            )
            else -> emptyList()
        }
    }

    /** The company [tile] pairs with (empty for others). */
    fun partnerName(tile: Int): String = CityBazaarBoard.tile(tile).partner?.let { CityBazaarBoard.tile(it).name }.orEmpty()

    /** How many of [tile]'s colour [seat] owns (companies aren't a colour set). */
    fun colourCount(match: CityBazaarMatch, seat: Int, tile: Int): Int {
        val group = CityBazaarBoard.tile(tile).group?.takeIf { it.isColourSet } ?: return 0
        return CityBazaarBoard.groupTiles(group).count { match.owners[it] == seat }
    }

    /** A tile's card as its owner (or anyone) sees it: tiers, rent now, next sale, costs. */
    fun propertyCard(match: CityBazaarMatch, tile: Int): CbPropertyCardUi {
        val spec = CityBazaarBoard.tile(tile)
        val owner = match.owners[tile]?.let { match.player(it) }
        val level = match.levels[tile] ?: 0
        val tierIndex = CityBazaarBoard.tier(tile, level) { match.owners[it] }
        val income = owner?.let { o ->
            val doubled = colourCount(match, o.seat, tile) >= SET_FOR_DOUBLE
            CityBazaarStrings.cbRentNow(CbFormat.rupees(rentNow(match, tile) ?: 0)) + (if (doubled) CityBazaarStrings.cbDoubleTag else "") + (if (o.halfRent) CityBazaarStrings.cbHalfTag else "")
        }
        val sale = owner?.let {
            if (level >= HOTEL_LEVEL) CityBazaarStrings.cbSellHotelLine(CbFormat.plus(spec.buildCost / RESALE_DIVISOR))
            else if (level > 0) CityBazaarStrings.cbSellHouseLine(CbFormat.plus(spec.buildCost / RESALE_DIVISOR))
            else CityBazaarStrings.cbSellBankValue(CbFormat.plus(spec.price / RESALE_DIVISOR))
        }
        val costs = buildList {
            add(CbSheetRowUi(CityBazaarStrings.cbPrice, CbFormat.rupees(spec.price), CbRowKind.NORMAL))
            if (spec.kind == TileKind.CITY) {
                add(CbSheetRowUi(CityBazaarStrings.cbHouseCost, CbFormat.rupees(spec.buildCost), CbRowKind.NORMAL))
                add(CbSheetRowUi(CityBazaarStrings.cbHotelCost, CbFormat.rupees(spec.buildCost), CbRowKind.NORMAL))
            }
        }
        return CbPropertyCardUi(
            tile = tile,
            name = spec.name,
            group = spec.group,
            kind = spec.kind,
            rows = rates(tile, if (owner != null) tierIndex else null),
            income = income,
            collected = if (spec.buyable) CityBazaarStrings.cbRentCollected(CbFormat.rupees(match.rentCollected[tile] ?: 0)) else null,
            sale = sale,
            costs = if (spec.buyable) costs else emptyList(),
            owner = when {
                !spec.buyable -> ""
                owner != null -> CityBazaarStrings.cbOwnedBy + " " + owner.name
                else -> CityBazaarStrings.cbUnowned
            },
            ownerColour = owner?.colour,
        )
    }

    fun properties(match: CityBazaarMatch, group: CityGroup?): CbPanel.Cities? {
        val me = match.me ?: return null
        val tiles = match.owners.filter { it.value == me.seat }.keys
            .filter { group == null || CityBazaarBoard.tile(it).group == group }
            .sorted()
        if (tiles.isEmpty()) return null
        return CbPanel.Cities(
            title = group?.let { CityBazaarStrings.cbYourColour(CbFormat.groupName(it)) } ?: CityBazaarStrings.cbYourCities,
            cities = ownedRows(match, tiles),
        )
    }

    /** Properties grouped by colour (board colour order, companies last), the most valuable first in each. */
    fun ownedRows(match: CityBazaarMatch, tiles: Collection<Int>): List<CbOwnedRowUi> =
        tiles.sortedWith(compareBy<Int> { CityBazaarBoard.tile(it).group?.ordinal ?: Int.MAX_VALUE }.thenByDescending { worth(match, it) })
            .map { tile ->
                val spec = CityBazaarBoard.tile(tile)
                CbOwnedRowUi(tile, spec.name, spec.group, spec.kind, CbFormat.rupees(rentNow(match, tile) ?: 0), CbFormat.rupees(worth(match, tile)))
            }

    /** What a property is worth: its price plus what its buildings cost. */
    fun worth(match: CityBazaarMatch, tile: Int): Int {
        val spec = CityBazaarBoard.tile(tile)
        return spec.price + (match.levels[tile] ?: 0) * spec.buildCost
    }

    /** Luck or Shagun Chest: the result of each dice total. */
    fun cardTable(deck: CardDeck): CbPanel.Deck {
        val luck = deck == CardDeck.LUCK
        return CbPanel.Deck(
            deck = deck,
            title = if (luck) CityBazaarStrings.cbCardTableTitle(CityBazaarStrings.cbLuckLabel, CityBazaarStrings.cbLuckGlyph) else CityBazaarStrings.cbCardTableTitle(CityBazaarStrings.cbChestLabel, CityBazaarStrings.cbChestGlyph),
            body = if (luck) CityBazaarStrings.cbLuckTableBody else CityBazaarStrings.cbChestTableBody,
            rows = CityBazaarCards.deck(deck).map {
                CbDeckRowUi(it.roll.toString(), it.text, cardAmount(it.effect), CityBazaarCards.isGain(deck, it.roll))
            },
        )
    }

    fun cardAmount(effect: CardEffect): String = when (effect) {
        is CardEffect.Money -> if (effect.amount >= 0) CbFormat.plus(effect.amount) else CbFormat.minus(-effect.amount)
        is CardEffect.CollectFromEach -> CbFormat.plus(effect.amount) + CityBazaarStrings.cbEachTag
        is CardEffect.PayEach -> CbFormat.minus(effect.amount) + CityBazaarStrings.cbEachTag
        is CardEffect.PerProperty -> CbFormat.minus(effect.amount) + CityBazaarStrings.cbPerPropertyTag
        else -> ""
    }

    /** What [tile] earns its owner now (2X with 3 of a colour, halved on half rent); null while unowned. */
    fun rentNow(match: CityBazaarMatch, tile: Int): Int? {
        val spec = CityBazaarBoard.tile(tile)
        val owner = match.owners[tile]?.let { match.player(it) } ?: return null
        val tier = CityBazaarBoard.tier(tile, match.levels[tile] ?: 0) { match.owners[it] }
        val base = spec.rents.getOrElse(tier) { 0 }
        val doubled = colourCount(match, owner.seat, tile) >= SET_FOR_DOUBLE
        return (if (doubled) base * 2 else base).let { if (owner.halfRent) it / 2 else it }
    }

    /** The bank: its money and every property nobody owns, cheapest rent first within each colour. */
    fun bankPanel(match: CityBazaarMatch): CbPanel.Bank {
        val free = CityBazaarBoard.tiles.filter { it.buyable && it.index !in match.owners }.map { it.index }
        val bank = match.bank
        val money = { amount: Long? -> amount?.let { CityBazaarStrings.cbRupees(RupeeFormat.digits(it)) } ?: CityBazaarStrings.cbBankUnknown }
        return CbPanel.Bank(
            name = CityBazaarStrings.cbBank,
            sheet = listOf(
                CbSheetRowUi(CityBazaarStrings.cbBankBalance, money(bank?.cash), CbRowKind.TOTAL),
                CbSheetRowUi(CityBazaarStrings.cbBankProperties, free.size.toString(), CbRowKind.NORMAL),
            ),
            cities = free.sortedWith(compareBy<Int> { CityBazaarBoard.tile(it).group?.ordinal ?: Int.MAX_VALUE }.thenBy { CityBazaarBoard.tile(it).price })
                .map { tile ->
                    val spec = CityBazaarBoard.tile(tile)
                    CbOwnedRowUi(tile, spec.name, spec.group, spec.kind, CbFormat.rupees(spec.rents.firstOrNull() ?: 0), CbFormat.rupees(spec.price))
                },
        )
    }

    /** A player's balance, worth and buildings, and each property with the rent it earns now. */
    fun playerPanel(match: CityBazaarMatch, seat: Int, cash: Int): CbPanel.Player? {
        val player = match.player(seat) ?: return null
        val tiles = match.owners.filter { it.value == seat }.keys.sorted()
        val levels = tiles.map { match.levels[it] ?: 0 }
        return CbPanel.Player(
            name = player.name.uppercase(),
            colour = player.colour,
            sheet = listOf(
                CbSheetRowUi(CityBazaarStrings.cbBalance, CbFormat.rupees(cash), CbRowKind.NORMAL),
                CbSheetRowUi(CityBazaarStrings.cbProperties, tiles.size.toString(), CbRowKind.NORMAL),
                CbSheetRowUi(CityBazaarStrings.cbHouses, levels.sumOf { if (it in 1 until HOTEL_LEVEL) it else 0 }.toString(), CbRowKind.NORMAL),
                CbSheetRowUi(CityBazaarStrings.cbHotels, levels.count { it >= HOTEL_LEVEL }.toString(), CbRowKind.NORMAL),
                CbSheetRowUi(CityBazaarStrings.cbRentCollectedLabel, CbFormat.rupees(player.rentCollected), CbRowKind.NORMAL),
                CbSheetRowUi(CityBazaarStrings.cbRentPaidTotalLabel, CbFormat.rupees(player.rentPaid), CbRowKind.NORMAL),
                CbSheetRowUi(CityBazaarStrings.cbNetWorth, CbFormat.rupees(match.netWorth[seat] ?: cash), CbRowKind.TOTAL),
            ),
            cities = ownedRows(match, tiles),
        )
    }

    fun tileInfo(match: CityBazaarMatch, tile: Int): CbPanel.Property? {
        val spec = CityBazaarBoard.tile(tile)
        if (!spec.buyable && spec.kind != TileKind.TAX) return null
        return CbPanel.Property(propertyCard(match, tile))
    }
}
