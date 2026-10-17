package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.Strings
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

/** Turns the match into what the City Bazaar table draws. Pure functions. */
object CityBazaarUiMapper {

    fun rupees(amount: Int): String = Strings.cbRupees(RupeeFormat.digits(amount))
    fun minus(amount: Int): String = Strings.cbMinusRupees(RupeeFormat.digits(amount))
    fun plus(amount: Int): String = Strings.cbPlusRupees(RupeeFormat.digits(amount))

    fun tiles(owners: Map<Int, Int>, levels: Map<Int, Int>, colourOf: (Int) -> CityBazaarColour?): List<CbTileUi> =
        CityBazaarBoard.tiles.map { CbTileUi(it.index, owners[it.index]?.let(colourOf), levels[it.index] ?: 0) }

    fun tokens(match: CityBazaarMatch, positions: Map<Int, Int>): List<CbTokenUi> =
        match.players.filter { !it.bankrupt }.map { CbTokenUi(it.seat, it.colour, positions[it.seat] ?: it.position) }

    /** What every card needs: [remainingMs] null while a move animates; [cash] as shown so far. */
    data class CardInputs(
        val speaking: Set<String>,
        val mutedForMe: Set<String>,
        val remainingMs: Long?,
        val turnSeconds: Long,
        val cash: Map<Int, Int>,
    )

    fun cards(match: CityBazaarMatch, inputs: CardInputs): List<CbPlayerCardUi> {
        val totalMs = inputs.turnSeconds * MILLIS
        val left = inputs.remainingMs?.coerceAtMost(totalMs) ?: totalMs
        val finished = match.phase == CityBazaarTurnPhase.FINISHED
        return match.players.map { p ->
            val isTurn = p.seat == match.currentSeat && !finished && !p.bankrupt
            val muted = p.userId in inputs.mutedForMe
            val place = match.ranking.indexOf(p.seat)
            CbPlayerCardUi(
                seat = p.seat,
                name = p.name,
                level = p.level.toString(),
                colour = p.colour,
                cash = inputs.cash[p.seat] ?: p.cash,
                isMe = p.isMe,
                isTurn = isTurn,
                talking = p.userId in inputs.speaking && p.micOn && !muted,
                timerFraction = if (isTurn) left.toFloat() / totalMs else 0f,
                status = when {
                    finished && place >= 0 -> Strings.ludoPlace(place + 1)
                    p.bankrupt -> Strings.cbStatusBankrupt
                    p.inJail && p.halfRent -> Strings.cbStatusHalf
                    p.inJail -> Strings.cbStatusJail
                    p.presence == CityBazaarPresence.AWAY -> Strings.cbStatusAway
                    p.presence == CityBazaarPresence.BOT -> Strings.cbStatusBot
                    else -> null
                },
                mutedForMe = muted,
                out = p.bankrupt,
            )
        }
    }

    /** Card columns: every other player on the left in turn order, the user on the right. Watching: half each side. */
    fun sides(match: CityBazaarMatch, cards: List<CbPlayerCardUi>): Triple<CbPlayerCardUi?, List<CbPlayerCardUi>, List<CbPlayerCardUi>> {
        val mine = cards.firstOrNull { it.isMe }
        val start = match.me?.seat ?: 0
        val others = cards.filterNot { it.isMe }.sortedBy { (it.seat - start + match.players.size) % match.players.size }
        if (mine != null) return Triple(mine, others, emptyList())
        val rightCount = (others.size + 1) / 2
        return Triple(null, others.drop(rightCount), others.take(rightCount))
    }

    fun audience(match: CityBazaarMatch, speaking: Set<String>, mutedForMe: Set<String>): List<TableAvatarUi> =
        match.spectators.map {
            val ring = when {
                !it.micOn -> AvatarRing.MUTED
                it.userId in speaking && it.userId !in mutedForMe -> AvatarRing.TALKING
                else -> AvatarRing.IDLE
            }
            TableAvatarUi(it.userId, it.name, ring)
        }

    fun elapsed(time: Duration): String {
        val total = time.inWholeSeconds.coerceAtLeast(0)
        return Strings.ludoClock(total / SECONDS_PER_MINUTE, total % SECONDS_PER_MINUTE)
    }

    fun countdown(seconds: Long): String = Strings.seatCountdown(seconds.coerceAtLeast(0))

    fun levelName(level: Int): String = if (level >= HOTEL_LEVEL) Strings.cbHotelLevel else Strings.cbHouseLevel(level)

    fun groupName(group: CityGroup): String = when (group) {
        CityGroup.RED -> Strings.cbGroupRed
        CityGroup.BLUE -> Strings.cbGroupBlue
        CityGroup.GREEN -> Strings.cbGroupGreen
        CityGroup.YELLOW -> Strings.cbGroupYellow
        CityGroup.GREY -> Strings.cbGroupGrey
    }

    // ---------------- cards ----------------

    /** The tiers of [tile] with [current] highlighted (cities: land, 1–3 houses, hotel; others: one fixed rent). */
    fun rates(tile: Int, current: Int?): List<CbRateRowUi> {
        val spec = CityBazaarBoard.tile(tile)
        return when (spec.kind) {
            TileKind.CITY -> listOf(CbRateRowUi(Strings.cbRentLand, rupees(spec.rents[0]), current == 0)) +
                (1 until HOTEL_LEVEL).map { CbRateRowUi(Strings.cbRentHouses(it), rupees(spec.rents[it]), current == it) } +
                CbRateRowUi(Strings.cbRentHotel, rupees(spec.rents[HOTEL_LEVEL]), current == HOTEL_LEVEL)
            TileKind.COMPANY -> listOf(
                CbRateRowUi(Strings.cbRentLand, rupees(spec.rents[0]), current == 0),
                CbRateRowUi(Strings.cbRentWithPartner(partnerName(tile)), rupees(spec.rents[1]), current == 1),
            )
            TileKind.TAX -> listOf(
                CbRateRowUi(
                    Strings.cbTax,
                    if (spec.taxPerProperty > 0) Strings.cbPerProperty(RupeeFormat.digits(spec.taxPerProperty)) else Strings.cbPerBuilding(RupeeFormat.digits(spec.taxPerBuilding)),
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
            Strings.cbRentNow(rupees(rentNow(match, tile) ?: 0)) + (if (doubled) Strings.cbDoubleTag else "") + (if (o.halfRent) Strings.cbHalfTag else "")
        }
        val sale = owner?.let {
            if (level >= HOTEL_LEVEL) Strings.cbSellHotelLine(plus(spec.buildCost / RESALE_DIVISOR))
            else if (level > 0) Strings.cbSellHouseLine(plus(spec.buildCost / RESALE_DIVISOR))
            else Strings.cbSellBankValue(plus(spec.price / RESALE_DIVISOR))
        }
        val costs = buildList {
            add(CbSheetRowUi(Strings.cbPrice, rupees(spec.price), CbRowKind.NORMAL))
            if (spec.kind == TileKind.CITY) {
                add(CbSheetRowUi(Strings.cbHouseCost, rupees(spec.buildCost), CbRowKind.NORMAL))
                add(CbSheetRowUi(Strings.cbHotelCost, rupees(spec.buildCost), CbRowKind.NORMAL))
            }
        }
        return CbPropertyCardUi(
            tile = tile,
            name = spec.name,
            group = spec.group,
            kind = spec.kind,
            rows = rates(tile, if (owner != null) tierIndex else null),
            income = income,
            sale = sale,
            costs = if (spec.buyable) costs else emptyList(),
            owner = when {
                !spec.buyable -> ""
                owner != null -> Strings.cbOwnedBy + " " + owner.name
                else -> Strings.cbUnowned
            },
            ownerColour = owner?.colour,
        )
    }

    /** The user's cities stacked by colour, in board colour order. */
    fun stacks(match: CityBazaarMatch): List<CbStackUi> {
        val me = match.me ?: return emptyList()
        val mine = match.owners.filter { it.value == me.seat }.keys
        return CityGroup.entries.mapNotNull { group ->
            val count = CityBazaarBoard.groupTiles(group).count { it in mine }
            if (count == 0) null else CbStackUi(group, count, groupName(group))
        }
    }

    fun properties(match: CityBazaarMatch, group: CityGroup?): CbPanel.Cities? {
        val me = match.me ?: return null
        val tiles = match.owners.filter { it.value == me.seat }.keys
            .filter { group == null || CityBazaarBoard.tile(it).group == group }
            .sorted()
        if (tiles.isEmpty()) return null
        return CbPanel.Cities(
            title = group?.let { Strings.cbYourColour(groupName(it)) } ?: Strings.cbYourCities,
            cards = tiles.map { propertyCard(match, it) },
        )
    }

    /** Luck or Shagun Chest: the result of each dice total. */
    fun cardTable(deck: CardDeck): CbPanel.Deck {
        val luck = deck == CardDeck.LUCK
        return CbPanel.Deck(
            deck = deck,
            title = if (luck) Strings.cbCardTableTitle(Strings.cbLuckLabel, Strings.cbLuckGlyph) else Strings.cbCardTableTitle(Strings.cbChestLabel, Strings.cbChestGlyph),
            body = if (luck) Strings.cbLuckTableBody else Strings.cbChestTableBody,
            rows = CityBazaarCards.deck(deck).map {
                CbRateRowUi(Strings.cbRollRow(it.roll, it.text), cardAmount(it.effect), false, gain = CityBazaarCards.isGain(deck, it.roll))
            },
        )
    }

    private fun cardAmount(effect: CardEffect): String = when (effect) {
        is CardEffect.Money -> if (effect.amount >= 0) plus(effect.amount) else minus(-effect.amount)
        is CardEffect.CollectFromEach -> plus(effect.amount) + Strings.cbEachTag
        is CardEffect.PayEach -> minus(effect.amount) + Strings.cbEachTag
        is CardEffect.PerProperty -> minus(effect.amount) + Strings.cbPerPropertyTag
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

    /** A player's balance, worth and buildings, and each property with the rent it earns now. */
    fun playerPanel(match: CityBazaarMatch, seat: Int, cash: Int): CbPanel.Player? {
        val player = match.player(seat) ?: return null
        val tiles = match.owners.filter { it.value == seat }.keys.sorted()
        val levels = tiles.map { match.levels[it] ?: 0 }
        return CbPanel.Player(
            name = player.name.uppercase(),
            colour = player.colour,
            sheet = listOf(
                CbSheetRowUi(Strings.cbBalance, rupees(cash), CbRowKind.NORMAL),
                CbSheetRowUi(Strings.cbProperties, tiles.size.toString(), CbRowKind.NORMAL),
                CbSheetRowUi(Strings.cbHouses, levels.sumOf { if (it in 1 until HOTEL_LEVEL) it else 0 }.toString(), CbRowKind.NORMAL),
                CbSheetRowUi(Strings.cbHotels, levels.count { it >= HOTEL_LEVEL }.toString(), CbRowKind.NORMAL),
                CbSheetRowUi(Strings.cbNetWorth, rupees(match.netWorth[seat] ?: cash), CbRowKind.TOTAL),
            ),
            cities = tiles.map { t ->
                val spec = CityBazaarBoard.tile(t)
                CbOwnedRowUi(t, spec.name, spec.group, spec.kind, rupees(rentNow(match, t) ?: 0))
            },
        )
    }

    /** Someone else's payment, for everyone: who paid whom (or the bank), how much, and why. */
    fun paymentToast(payer: String, payee: String?, reason: String, amount: Int): CbToastUi = CbToastUi(
        0,
        Strings.cbPaid(payer),
        listOf(
            CbSheetRowUi(if (payee != null) Strings.cbPaidTo(payer, payee) else Strings.cbPaidBank(payer), minus(amount), CbRowKind.MINUS),
            CbSheetRowUi(reason, "", CbRowKind.NORMAL),
        ),
        gain = false,
    )

    fun tileInfo(match: CityBazaarMatch, tile: Int): CbPanel.Property? {
        val spec = CityBazaarBoard.tile(tile)
        if (!spec.buyable && spec.kind != TileKind.TAX) return null
        return CbPanel.Property(propertyCard(match, tile))
    }

    // ---------------- decisions ----------------

    private fun sheet(balance: Int, label: String, amount: Int, gain: Boolean): List<CbSheetRowUi> = listOf(
        CbSheetRowUi(Strings.cbCurrentBalance, rupees(balance), CbRowKind.NORMAL),
        CbSheetRowUi(label, if (gain) plus(amount) else minus(amount), if (gain) CbRowKind.PLUS else CbRowKind.MINUS),
        CbSheetRowUi(Strings.cbRemaining, rupees(if (gain) balance + amount else balance - amount), CbRowKind.TOTAL),
    )

    fun buy(me: CityBazaarPlayer, tile: Int, countdown: String): CbModal.Buy {
        val spec = CityBazaarBoard.tile(tile)
        return CbModal.Buy(
            tile = tile,
            title = Strings.cbBuyTitle(spec.name),
            group = spec.group,
            kind = spec.kind,
            rates = rates(tile, null),
            sheet = sheet(me.cash, Strings.cbPropertyCost, spec.price, gain = false),
            note = if (spec.kind == TileKind.CITY) Strings.cbBuyNote(rupees(spec.rents[0]), rupees(spec.rents[1])) else Strings.cbPairNote(partnerName(tile)),
            canAfford = me.cash >= spec.price,
            countdown = countdown,
        )
    }

    fun build(match: CityBazaarMatch, me: CityBazaarPlayer, tile: Int, countdown: String): CbModal.Build {
        val spec = CityBazaarBoard.tile(tile)
        val level = match.levels[tile] ?: 0
        val next = level + 1
        return CbModal.Build(
            tile = tile,
            title = Strings.cbBuildTitle(if (next >= HOTEL_LEVEL) Strings.cbBuildHotel else Strings.cbBuildHouse(next), spec.name),
            group = spec.group,
            rates = rates(tile, null).mapIndexed { i, row -> row.copy(highlighted = i == next) }.filterIndexed { i, _ -> i == level || i == next },
            sheet = sheet(me.cash, Strings.cbUpgradeCost(levelName(next)), spec.buildCost, gain = false),
            note = Strings.cbBuildNote(plus(spec.rents[next] - spec.rents[level])),
            canAfford = me.cash >= spec.buildCost,
            countdown = countdown,
        )
    }

    fun jailRoll(match: CityBazaarMatch, countdown: String): CbModal.JailRoll =
        CbModal.JailRoll(Strings.cbJailRollBody(match.jailTriesLeft), countdown)

    fun jailChoice(me: CityBazaarPlayer, countdown: String): CbModal.JailChoice = CbModal.JailChoice(me.cash >= JAIL_FINE, countdown)

    /** What the user can sell: the top building of each built city (hotel first), or bare cities. */
    fun assets(match: CityBazaarMatch, seat: Int): List<CbAssetUi> =
        match.owners.filter { it.value == seat }.keys.sorted().map { tile ->
            val spec = CityBazaarBoard.tile(tile)
            val level = match.levels[tile] ?: 0
            if (level > 0) {
                CbAssetUi(BUILDING + tile, tile, spec.name, spec.group, spec.kind, if (level >= HOTEL_LEVEL) Strings.cbSellHotel else Strings.cbSellHouse, plus(spec.buildCost / RESALE_DIVISOR))
            } else {
                CbAssetUi(TILE + tile, tile, spec.name, spec.group, spec.kind, Strings.cbSellProperty, plus(spec.price / RESALE_DIVISOR))
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
            body = Strings.cbDebtBody(rupees(deficit)),
            assets = assets(match, me.seat),
            selected = selected,
            tracker = Strings.cbSelectedValue(rupees(picked), rupees(deficit)),
            enough = picked >= deficit,
            canAuction = selected.size == 1 && !isBuilding(selected.first()),
            countdown = countdown,
        )
    }

    /** The auction prompt: the card, who holds this colour, and the bid sheet for [viewer]. */
    fun auction(match: CityBazaarMatch, auction: CityBazaarAuctionInfo, viewer: CityBazaarPlayer, countdown: String): CbModal.Auction {
        val spec = CityBazaarBoard.tile(auction.tile)
        val seller = match.player(auction.sellerSeat)?.name.orEmpty()
        val isSeller = viewer.seat == auction.sellerSeat
        val pairLine = spec.partner?.let { p -> listOf(Strings.cbPairLine(CityBazaarBoard.tile(p).name, match.owners[p]?.let { match.player(it)?.name })) }
        val colourLines = pairLine ?: spec.group?.takeIf { it.isColourSet }?.let { group ->
            val others = CityBazaarBoard.groupTiles(group).filter { it != auction.tile }
            val byOwner = others.mapNotNull { t -> match.owners[t]?.let { it to t } }.groupBy({ it.first }, { it.second })
            buildList {
                add(Strings.cbYouOwnColour(byOwner[viewer.seat]?.size ?: 0))
                byOwner.filterKeys { it != viewer.seat }.forEach { (seat, tiles) ->
                    val name = match.player(seat)?.name.orEmpty()
                    add(Strings.cbOwnsColour(name, tiles.joinToString { CityBazaarBoard.tile(it).name }))
                    if (tiles.size + 1 >= SET_FOR_DOUBLE) add(Strings.cbColourRisk(name))
                }
            }
        }.orEmpty()
        val leader = auction.leaderSeat?.let { match.player(it)?.name }
        val step = auction.nextBid - auction.bid
        val sheet = listOf(
            CbSheetRowUi(if (leader == null) Strings.cbOpeningBid else Strings.cbCurrentBid, rupees(auction.bid), CbRowKind.NORMAL),
            CbSheetRowUi(Strings.cbBidStep, if (leader == null) plus(0) else plus(step), CbRowKind.NORMAL),
            CbSheetRowUi(Strings.cbYourBid, rupees(auction.nextBid), CbRowKind.MINUS),
            CbSheetRowUi(Strings.cbBalanceAfter, rupees(viewer.cash - auction.nextBid), CbRowKind.TOTAL),
        )
        return CbModal.Auction(
            tile = auction.tile,
            title = Strings.cbAuctionTitle(spec.name),
            body = if (isSeller) Strings.cbAuctionYours else Strings.cbAuctionBody(seller),
            card = propertyCard(match, auction.tile),
            colourLines = colourLines,
            leader = leader?.let { Strings.cbHighest(it) + " · " + rupees(auction.bid) } ?: Strings.cbNoBidsYet,
            sheet = sheet,
            bidLabel = Strings.cbBid(rupees(auction.nextBid)),
            isSeller = isSeller,
            canBid = !isSeller && auction.leaderSeat != viewer.seat && viewer.cash >= auction.nextBid,
            countdown = countdown,
        )
    }

    fun result(match: CityBazaarMatch): CbModal.Result = CbModal.Result(
        match.ranking.mapIndexedNotNull { index, seat ->
            match.player(seat)?.let { CbResultRowUi(Strings.ludoPlace(index + 1), it.name, it.colour, rupees(match.netWorth[seat] ?: 0)) }
        },
    )

    fun moneyToast(title: String, balance: Int, label: String, amount: Int, gain: Boolean): CbToastUi =
        CbToastUi(0, title, sheet(balance, label, amount, gain), gain)

    fun noteToast(title: String, line: String, gain: Boolean = true): CbToastUi =
        CbToastUi(0, title, listOf(CbSheetRowUi(line, "", CbRowKind.NORMAL)), gain)

    private const val MILLIS = 1_000L
    private const val SECONDS_PER_MINUTE = 60L
    private const val BUILDING = "b"
    private const val TILE = "t"
}
