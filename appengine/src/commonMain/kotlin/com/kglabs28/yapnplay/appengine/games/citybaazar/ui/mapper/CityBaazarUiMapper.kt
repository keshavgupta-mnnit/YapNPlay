package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.table.AvatarRing
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarAuctionInfo
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarColour
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarMatch
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarPlayer
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarPresence
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarTurnPhase
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoard
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityGroup
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.HOTEL_LEVEL
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.JAIL_FINE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.RESALE_DIVISOR
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.RupeeFormat
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.SET_FOR_DOUBLE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbAssetUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbModal
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbPlayerCardUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbPropertyCardUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbRateRowUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbResultRowUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbRowKind
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbSheetRowUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbStackUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbTileUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbToastUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbTokenUi
import kotlin.time.Duration

/** Turns the match into what the City Baazar table draws. Pure functions. */
object CityBaazarUiMapper {

    fun rupees(amount: Int): String = Strings.cbRupees(RupeeFormat.digits(amount))
    fun minus(amount: Int): String = Strings.cbMinusRupees(RupeeFormat.digits(amount))
    fun plus(amount: Int): String = Strings.cbPlusRupees(RupeeFormat.digits(amount))

    fun tiles(owners: Map<Int, Int>, levels: Map<Int, Int>, colourOf: (Int) -> CityBaazarColour?): List<CbTileUi> =
        CityBaazarBoard.tiles.map { CbTileUi(it.index, owners[it.index]?.let(colourOf), levels[it.index] ?: 0) }

    fun tokens(match: CityBaazarMatch, positions: Map<Int, Int>): List<CbTokenUi> =
        match.players.filter { !it.bankrupt }.map { CbTokenUi(it.seat, it.colour, positions[it.seat] ?: it.position) }

    /** What every card needs: [remainingMs] null while a move animates; [cash] as shown so far. */
    data class CardInputs(
        val speaking: Set<String>,
        val mutedForMe: Set<String>,
        val remainingMs: Long?,
        val turnSeconds: Long,
        val cash: Map<Int, Int>,
    )

    fun cards(match: CityBaazarMatch, inputs: CardInputs): List<CbPlayerCardUi> {
        val totalMs = inputs.turnSeconds * MILLIS
        val left = inputs.remainingMs?.coerceAtMost(totalMs) ?: totalMs
        val finished = match.phase == CityBaazarTurnPhase.FINISHED
        return match.players.map { p ->
            val isTurn = p.seat == match.currentSeat && !finished && !p.bankrupt
            val muted = p.userId in inputs.mutedForMe
            val place = match.ranking.indexOf(p.seat)
            CbPlayerCardUi(
                seat = p.seat,
                name = p.name,
                level = p.level.toString(),
                colour = p.colour,
                cash = rupees(inputs.cash[p.seat] ?: p.cash),
                isMe = p.isMe,
                isTurn = isTurn,
                talking = p.userId in inputs.speaking && p.micOn && !muted,
                timerFraction = if (isTurn) left.toFloat() / totalMs else 0f,
                status = when {
                    finished && place >= 0 -> Strings.ludoPlace(place + 1)
                    p.bankrupt -> Strings.cbStatusBankrupt
                    p.inJail && p.halfRent -> Strings.cbStatusHalf
                    p.inJail -> Strings.cbStatusJail
                    p.presence == CityBaazarPresence.AWAY -> Strings.cbStatusAway
                    p.presence == CityBaazarPresence.BOT -> Strings.cbStatusBot
                    else -> null
                },
                mutedForMe = muted,
                out = p.bankrupt,
            )
        }
    }

    /** Card columns: the user bottom-left; the others in turn order, the first half on the right. Watching: half each side. */
    fun sides(match: CityBaazarMatch, cards: List<CbPlayerCardUi>): Triple<CbPlayerCardUi?, List<CbPlayerCardUi>, List<CbPlayerCardUi>> {
        val mine = cards.firstOrNull { it.isMe }
        val start = match.me?.seat ?: 0
        val others = cards.filterNot { it.isMe }.sortedBy { (it.seat - start + match.players.size) % match.players.size }
        val rightCount = (others.size + 1) / 2
        return Triple(mine, others.drop(rightCount), others.take(rightCount))
    }

    fun audience(match: CityBaazarMatch, speaking: Set<String>, mutedForMe: Set<String>): List<TableAvatarUi> =
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
        CityGroup.PURPLE -> Strings.cbGroupPurple
        CityGroup.ORANGE -> Strings.cbGroupOrange
    }

    // ---------------- cards ----------------

    /** The tiers of [tile] with [current] highlighted (cities: land, 1–3 houses, hotel; others: one fixed rent). */
    fun rates(tile: Int, current: Int?): List<CbRateRowUi> {
        val spec = CityBaazarBoard.tile(tile)
        return when (spec.kind) {
            TileKind.CITY -> listOf(CbRateRowUi(Strings.cbRentLand, rupees(spec.rents[0]), current == 0)) +
                (1 until HOTEL_LEVEL).map { CbRateRowUi(Strings.cbRentHouses(it), rupees(spec.rents[it]), current == it) } +
                CbRateRowUi(Strings.cbRentHotel, rupees(spec.rents[HOTEL_LEVEL]), current == HOTEL_LEVEL)
            TileKind.TRANSIT, TileKind.UTILITY -> listOf(
                CbRateRowUi(Strings.cbRentLand, rupees(spec.rents[0]), current == 0),
                CbRateRowUi(Strings.cbRentColourDouble, rupees(spec.rents[0] * 2), false),
            )
            TileKind.TAX -> listOf(CbRateRowUi(Strings.cbTax, rupees(spec.tax), true))
            else -> emptyList()
        }
    }

    /** How many of [tile]'s colour [seat] owns. */
    fun colourCount(match: CityBaazarMatch, seat: Int, tile: Int): Int {
        val group = CityBaazarBoard.tile(tile).group ?: return 0
        return CityBaazarBoard.groupTiles(group).count { match.owners[it] == seat }
    }

    /** A tile's card as its owner (or anyone) sees it: tiers, rent now, next sale, costs. */
    fun propertyCard(match: CityBaazarMatch, tile: Int): CbPropertyCardUi {
        val spec = CityBaazarBoard.tile(tile)
        val owner = match.owners[tile]?.let { match.player(it) }
        val level = match.levels[tile] ?: 0
        val tierIndex = if (spec.kind == TileKind.CITY) level else 0
        val income = owner?.let { o ->
            val base = spec.rents.getOrElse(tierIndex) { 0 }
            val doubled = colourCount(match, o.seat, tile) >= SET_FOR_DOUBLE
            val now = (if (doubled) base * 2 else base).let { if (o.halfRent) it / 2 else it }
            Strings.cbRentNow(rupees(now)) + (if (doubled) Strings.cbDoubleTag else "") + (if (o.halfRent) Strings.cbHalfTag else "")
        }
        val sale = owner?.let {
            if (level > 0) Strings.cbSellNext(levelName(level), plus(spec.buildCost / RESALE_DIVISOR))
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
        )
    }

    /** The user's cities stacked by colour, in board colour order. */
    fun stacks(match: CityBaazarMatch): List<CbStackUi> {
        val me = match.me ?: return emptyList()
        val mine = match.owners.filter { it.value == me.seat }.keys
        return CityGroup.entries.mapNotNull { group ->
            val count = CityBaazarBoard.groupTiles(group).count { it in mine }
            if (count == 0) null else CbStackUi(group, count, groupName(group))
        }
    }

    fun properties(match: CityBaazarMatch, group: CityGroup?): CbModal.Properties? {
        val me = match.me ?: return null
        val tiles = match.owners.filter { it.value == me.seat }.keys
            .filter { group == null || CityBaazarBoard.tile(it).group == group }
            .sorted()
        if (tiles.isEmpty()) return null
        return CbModal.Properties(
            title = group?.let { Strings.cbYourColour(groupName(it)) } ?: Strings.cbYourCities,
            cards = tiles.map { propertyCard(match, it) },
        )
    }

    fun tileInfo(match: CityBaazarMatch, tile: Int): CbModal.TileInfo? {
        val spec = CityBaazarBoard.tile(tile)
        if (!spec.buyable && spec.kind != TileKind.TAX) return null
        return CbModal.TileInfo(propertyCard(match, tile))
    }

    // ---------------- decisions ----------------

    private fun sheet(balance: Int, label: String, amount: Int, gain: Boolean): List<CbSheetRowUi> = listOf(
        CbSheetRowUi(Strings.cbCurrentBalance, rupees(balance), CbRowKind.NORMAL),
        CbSheetRowUi(label, if (gain) plus(amount) else minus(amount), if (gain) CbRowKind.PLUS else CbRowKind.MINUS),
        CbSheetRowUi(Strings.cbRemaining, rupees(if (gain) balance + amount else balance - amount), CbRowKind.TOTAL),
    )

    fun buy(me: CityBaazarPlayer, tile: Int, countdown: String): CbModal.Buy {
        val spec = CityBaazarBoard.tile(tile)
        return CbModal.Buy(
            tile = tile,
            title = Strings.cbLandedOn(spec.name),
            group = spec.group,
            kind = spec.kind,
            rates = rates(tile, null),
            sheet = sheet(me.cash, Strings.cbPropertyCost, spec.price, gain = false),
            note = if (spec.kind == TileKind.CITY) Strings.cbBuyNote(rupees(spec.rents[0]), rupees(spec.rents[1])) else Strings.cbFixedRentNote,
            canAfford = me.cash >= spec.price,
            countdown = countdown,
        )
    }

    fun build(match: CityBaazarMatch, me: CityBaazarPlayer, tile: Int, countdown: String): CbModal.Build {
        val spec = CityBaazarBoard.tile(tile)
        val level = match.levels[tile] ?: 0
        val next = level + 1
        return CbModal.Build(
            tile = tile,
            title = Strings.cbUpgrade(spec.name),
            group = spec.group,
            rates = rates(tile, null).mapIndexed { i, row -> row.copy(highlighted = i == next) }.filterIndexed { i, _ -> i == level || i == next },
            sheet = sheet(me.cash, Strings.cbUpgradeCost(levelName(next)), spec.buildCost, gain = false),
            note = Strings.cbBuildNote(plus(spec.rents[next] - spec.rents[level])),
            canAfford = me.cash >= spec.buildCost,
            countdown = countdown,
        )
    }

    fun jailRoll(match: CityBaazarMatch, countdown: String): CbModal.JailRoll =
        CbModal.JailRoll(Strings.cbJailRollBody(match.jailTriesLeft), countdown)

    fun jailChoice(me: CityBaazarPlayer, countdown: String): CbModal.JailChoice = CbModal.JailChoice(me.cash >= JAIL_FINE, countdown)

    /** What the user can sell: the top building of each built city (hotel first), or bare cities. */
    fun assets(match: CityBaazarMatch, seat: Int): List<CbAssetUi> =
        match.owners.filter { it.value == seat }.keys.sorted().map { tile ->
            val spec = CityBaazarBoard.tile(tile)
            val level = match.levels[tile] ?: 0
            if (level > 0) {
                CbAssetUi(BUILDING + tile, tile, spec.name, spec.group, spec.kind, Strings.cbSellBuilding(levelName(level)), plus(spec.buildCost / RESALE_DIVISOR))
            } else {
                CbAssetUi(TILE + tile, tile, spec.name, spec.group, spec.kind, Strings.cbSellProperty, plus(spec.price / RESALE_DIVISOR))
            }
        }

    fun assetValue(id: String): Int {
        val spec = CityBaazarBoard.tile(assetTile(id))
        return if (isBuilding(id)) spec.buildCost / RESALE_DIVISOR else spec.price / RESALE_DIVISOR
    }

    fun isBuilding(id: String): Boolean = id.startsWith(BUILDING)

    fun assetTile(id: String): Int = id.drop(1).toInt()

    fun debt(match: CityBaazarMatch, me: CityBaazarPlayer, selected: Set<String>, countdown: String): CbModal.Debt? {
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
    fun auction(match: CityBaazarMatch, auction: CityBaazarAuctionInfo, viewer: CityBaazarPlayer, countdown: String): CbModal.Auction {
        val spec = CityBaazarBoard.tile(auction.tile)
        val seller = match.player(auction.sellerSeat)?.name.orEmpty()
        val isSeller = viewer.seat == auction.sellerSeat
        val colourLines = spec.group?.let { group ->
            val others = CityBaazarBoard.groupTiles(group).filter { it != auction.tile }
            val byOwner = others.mapNotNull { t -> match.owners[t]?.let { it to t } }.groupBy({ it.first }, { it.second })
            buildList {
                add(Strings.cbYouOwnColour(byOwner[viewer.seat]?.size ?: 0))
                byOwner.filterKeys { it != viewer.seat }.forEach { (seat, tiles) ->
                    val name = match.player(seat)?.name.orEmpty()
                    add(Strings.cbOwnsColour(name, tiles.joinToString { CityBaazarBoard.tile(it).name }))
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

    fun result(match: CityBaazarMatch): CbModal.Result = CbModal.Result(
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
