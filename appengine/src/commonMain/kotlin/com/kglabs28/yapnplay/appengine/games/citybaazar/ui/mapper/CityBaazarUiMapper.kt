package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.table.AvatarRing
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarColour
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarMatch
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarPlayer
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarPresence
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarTurnPhase
import com.kglabs28.yapnplay.appengine.games.citybaazar.engine.CityBaazarEconomy
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoard
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.HOTEL_LEVEL
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.JAIL_FINE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.MAX_JAIL_TRIES
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.RupeeFormat
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TRANSIT_RENTS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.UTILITY_RENT_BOTH
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.UTILITY_RENT_ONE
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbAssetUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbBuildOptionUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbModal
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbPlayerCardUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbRateRowUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbResultRowUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbRowKind
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbSheetRowUi
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

    /**
     * Card columns: the user bottom-left; the others in turn order after
     * them, the first half on the right and the rest on the left above the
     * user. Watching: half on each side.
     */
    fun sides(match: CityBaazarMatch, cards: List<CbPlayerCardUi>): Triple<CbPlayerCardUi?, List<CbPlayerCardUi>, List<CbPlayerCardUi>> {
        val mine = cards.firstOrNull { it.isMe }
        val start = match.me?.seat ?: 0
        val others = cards.filterNot { it.isMe }.sortedBy { (it.seat - start + match.players.size) % match.players.size }
        val rightCount = (others.size + 1) / 2
        return if (mine == null) {
            Triple(null, others.drop(rightCount), others.take(rightCount))
        } else {
            Triple(mine, others.drop(rightCount), others.take(rightCount))
        }
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

    // ---------------- rate cards ----------------

    fun levelName(level: Int): String = if (level >= HOTEL_LEVEL) Strings.cbRentHotel else Strings.cbHouseLevel(level)

    /** The full rate card of [tile]; [current] highlights a tier (city level or transit count). */
    fun rates(tile: Int, current: Int?): List<CbRateRowUi> {
        val spec = CityBaazarBoard.tile(tile)
        return when (spec.kind) {
            TileKind.CITY -> listOf(
                CbRateRowUi(Strings.cbRentLand, rupees(spec.rents[0]), current == 0),
                CbRateRowUi(Strings.cbRentSet, rupees(spec.rents[0] * 2), false),
            ) + (1 until HOTEL_LEVEL).map { CbRateRowUi(Strings.cbRentHouses(it), rupees(spec.rents[it]), current == it) } +
                CbRateRowUi(Strings.cbRentHotel, rupees(spec.rents[HOTEL_LEVEL]), current == HOTEL_LEVEL)
            TileKind.TRANSIT -> TRANSIT_RENTS.mapIndexed { i, rent -> CbRateRowUi(Strings.cbTransitOwned(i + 1), rupees(rent), current == i + 1) }
            TileKind.UTILITY -> listOf(
                CbRateRowUi(Strings.cbUtilityOne, Strings.cbDiceTimes(rupees(UTILITY_RENT_ONE)), current == 1),
                CbRateRowUi(Strings.cbUtilityBoth, Strings.cbDiceTimes(rupees(UTILITY_RENT_BOTH)), current == 2),
            )
            TileKind.TAX -> listOf(CbRateRowUi(Strings.cbTax, rupees(spec.tax), true))
            else -> emptyList()
        }
    }

    private fun sheet(balance: Int, label: String, amount: Int, gain: Boolean): List<CbSheetRowUi> = listOf(
        CbSheetRowUi(Strings.cbCurrentBalance, rupees(balance), CbRowKind.NORMAL),
        CbSheetRowUi(label, if (gain) plus(amount) else minus(amount), if (gain) CbRowKind.PLUS else CbRowKind.MINUS),
        CbSheetRowUi(Strings.cbRemaining, rupees(if (gain) balance + amount else balance - amount), CbRowKind.TOTAL),
    )

    /** "Landed on Goa": rate card, balance sheet and a note on what it can earn. */
    fun buy(me: CityBaazarPlayer, tile: Int, countdown: String): CbModal.Buy {
        val spec = CityBaazarBoard.tile(tile)
        return CbModal.Buy(
            title = Strings.cbLandedOn(spec.name),
            tileName = spec.name,
            group = spec.group,
            kind = spec.kind,
            rates = rates(tile, null),
            sheet = sheet(me.cash, Strings.cbPropertyCost, spec.price, gain = false),
            note = when (spec.kind) {
                TileKind.TRANSIT -> Strings.cbTransitNote
                TileKind.UTILITY -> Strings.cbUtilityNote
                else -> Strings.cbBuyNote(rupees(spec.rents[0]), rupees(spec.rents[1]))
            },
            canAfford = me.cash >= spec.price,
            countdown = countdown,
        )
    }

    /** One more house (or the hotel) on [tile]: this tier vs the next, and the cost. */
    fun build(match: CityBaazarMatch, me: CityBaazarPlayer, tile: Int, fromLanding: Boolean, countdown: String): CbModal.Build {
        val spec = CityBaazarBoard.tile(tile)
        val level = match.levels[tile] ?: 0
        val next = level + 1
        val nowRent = if (level == 0) spec.rents[0] * 2 else spec.rents[level]
        val nextRent = spec.rents[next]
        return CbModal.Build(
            tile = tile,
            title = Strings.cbUpgrade(spec.name),
            group = spec.group,
            rates = listOf(
                CbRateRowUi(if (level == 0) Strings.cbRentSet else Strings.cbRentHouses(level), rupees(nowRent), false),
                CbRateRowUi(if (next >= HOTEL_LEVEL) Strings.cbRentHotel else Strings.cbRentHouses(next), rupees(nextRent), true),
            ),
            sheet = sheet(me.cash, Strings.cbUpgradeCost(levelName(next)), spec.buildCost, gain = false),
            note = Strings.cbBuildNote(plus(nextRent - nowRent)),
            canAfford = me.cash >= spec.buildCost,
            fromLanding = fromLanding,
            countdown = countdown,
        )
    }

    fun buildPicker(match: CityBaazarMatch): CbModal.BuildPicker = CbModal.BuildPicker(
        match.buildable.map { tile ->
            val spec = CityBaazarBoard.tile(tile)
            val next = (match.levels[tile] ?: 0) + 1
            CbBuildOptionUi(tile, spec.name, spec.group, Strings.cbBuildOption(levelName(next), rupees(spec.buildCost)))
        },
    )

    fun jail(me: CityBaazarPlayer, countdown: String): CbModal.Jail = CbModal.Jail(
        body = Strings.cbJailBody(MAX_JAIL_TRIES - me.jailTries),
        canPay = me.cash >= JAIL_FINE,
        hasPass = me.jailPasses > 0,
        countdown = countdown,
    )

    /** What the user can sell: the top building of each built city, or bare tiles. */
    fun assets(match: CityBaazarMatch, seat: Int): List<CbAssetUi> =
        match.owners.filter { it.value == seat }.keys.sorted().map { tile ->
            val spec = CityBaazarBoard.tile(tile)
            val level = match.levels[tile] ?: 0
            if (level > 0) {
                CbAssetUi(BUILDING + tile, tile, spec.name, spec.group, spec.kind, Strings.cbSellHouse(levelName(level).lowercase()), plus(CityBaazarEconomy.buildingResale(tile)))
            } else {
                CbAssetUi(TILE + tile, tile, spec.name, spec.group, spec.kind, Strings.cbSellProperty, plus(CityBaazarEconomy.tileResale(tile)))
            }
        }

    fun assetValue(id: String): Int {
        val tile = id.drop(1).toInt()
        return if (id.startsWith(BUILDING)) CityBaazarEconomy.buildingResale(tile) else CityBaazarEconomy.tileResale(tile)
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
            countdown = countdown,
        )
    }

    /** A tapped tile's rate card (cities, transit, utilities, taxes); null for the rest. */
    fun tileInfo(match: CityBaazarMatch, tile: Int): CbModal.TileInfo? {
        val spec = CityBaazarBoard.tile(tile)
        if (!spec.buyable && spec.kind != TileKind.TAX) return null
        val owner = match.owners[tile]?.let { match.player(it) }
        val current = when (spec.kind) {
            TileKind.CITY -> owner?.let { match.levels[tile] ?: 0 }
            TileKind.TRANSIT, TileKind.UTILITY -> owner?.let { o ->
                CityBaazarBoard.tilesOf(spec.kind).count { match.owners[it] == o.seat }
            }
            else -> null
        }
        return CbModal.TileInfo(
            title = spec.name.uppercase(),
            group = spec.group,
            kind = spec.kind,
            rates = rates(tile, current),
            owner = when {
                !spec.buyable -> ""
                owner != null -> Strings.cbOwnedBy + " " + owner.name
                else -> Strings.cbUnowned
            },
        )
    }

    fun result(match: CityBaazarMatch): CbModal.Result = CbModal.Result(
        match.ranking.mapIndexedNotNull { index, seat ->
            match.player(seat)?.let { CbResultRowUi(Strings.ludoPlace(index + 1), it.name, it.colour, rupees(match.netWorth[seat] ?: 0)) }
        },
    )

    /** A balance-sheet toast: balance before, the change, and what's left. */
    fun moneyToast(title: String, balance: Int, label: String, amount: Int, gain: Boolean): CbToastUi =
        CbToastUi(0, title, sheet(balance, label, amount, gain), gain)

    /** A one-line toast (someone else's card, a bankruptcy, trading soon). */
    fun noteToast(title: String, line: String): CbToastUi =
        CbToastUi(0, title, listOf(CbSheetRowUi(line, "", CbRowKind.NORMAL)), gain = true)

    private const val MILLIS = 1_000L
    private const val SECONDS_PER_MINUTE = 60L
    private const val BUILDING = "b"
    private const val TILE = "t"
}
