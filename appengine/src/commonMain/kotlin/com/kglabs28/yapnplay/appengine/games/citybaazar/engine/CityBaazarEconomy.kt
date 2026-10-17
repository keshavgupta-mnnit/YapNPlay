package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoard
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.HOTEL_LEVEL
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.RESALE_DIVISOR
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TRANSIT_RENTS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.UTILITY_RENT_BOTH
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.UTILITY_RENT_ONE

/** Rents, building rights and values. Pure functions over the state. */
object CityBaazarEconomy {

    fun ownsGroup(s: CityBaazarState, seat: Int, tile: Int): Boolean {
        val group = CityBaazarBoard.tile(tile).group ?: return false
        return CityBaazarBoard.groupTiles(group).all { s.owners[it] == seat }
    }

    /** [seat] may build on [tile] now (ignoring money). */
    fun buildAllowed(s: CityBaazarState, seat: Int, tile: Int): Boolean {
        val spec = CityBaazarBoard.tile(tile)
        return spec.kind == TileKind.CITY && s.owners[tile] == seat && s.level(tile) < HOTEL_LEVEL && ownsGroup(s, seat, tile)
    }

    /** Cities [seat] can build on now and afford. */
    fun buildable(s: CityBaazarState, seat: Int): List<Int> =
        s.owners.filter { it.value == seat }.keys
            .filter { buildAllowed(s, seat, it) && s.cashOf(seat) >= CityBaazarBoard.tile(it).buildCost }
            .sorted()

    /** Rent for landing on [tile] with [diceTotal] on the dice. */
    fun rent(s: CityBaazarState, tile: Int, diceTotal: Int): Int {
        val owner = s.owners[tile] ?: return 0
        val spec = CityBaazarBoard.tile(tile)
        return when (spec.kind) {
            TileKind.CITY -> {
                val level = s.level(tile)
                val land = spec.rents.first()
                when {
                    level > 0 -> spec.rents[level]
                    s.cbRules.house.setDoubleRent && ownsGroup(s, owner, tile) -> land * 2
                    else -> land
                }
            }
            TileKind.TRANSIT -> {
                val held = CityBaazarBoard.tilesOf(TileKind.TRANSIT).count { s.owners[it] == owner }
                TRANSIT_RENTS[(held - 1).coerceIn(0, TRANSIT_RENTS.lastIndex)]
            }
            TileKind.UTILITY -> {
                val held = CityBaazarBoard.tilesOf(TileKind.UTILITY).count { s.owners[it] == owner }
                diceTotal * if (held > 1) UTILITY_RENT_BOTH else UTILITY_RENT_ONE
            }
            else -> 0
        }
    }

    /** Rent a city would earn at [level] (0 land … 4 hotel), for rate cards. */
    fun cityRent(tile: Int, level: Int): Int = CityBaazarBoard.tile(tile).rents.getOrElse(level) { 0 }

    fun buildingResale(tile: Int): Int = CityBaazarBoard.tile(tile).buildCost / RESALE_DIVISOR

    fun tileResale(tile: Int): Int = CityBaazarBoard.tile(tile).price / RESALE_DIVISOR

    /** Everything [seat] could raise by selling to the bank. */
    fun liquidationValue(s: CityBaazarState, seat: Int): Int =
        s.owners.filter { it.value == seat }.keys.sumOf { tileResale(it) + s.level(it) * buildingResale(it) }

    /** Balance + tile prices + build cost of buildings (end-of-match ranking). */
    fun netWorth(s: CityBaazarState, seat: Int): Int =
        s.cashOf(seat) + s.owners.filter { it.value == seat }.keys.sumOf {
            CityBaazarBoard.tile(it).price + s.level(it) * CityBaazarBoard.tile(it).buildCost
        }

    fun houses(s: CityBaazarState, seat: Int): Int =
        s.owners.filter { it.value == seat }.keys.sumOf { s.level(it).let { l -> if (l in 1 until HOTEL_LEVEL) l else 0 } }

    fun hotels(s: CityBaazarState, seat: Int): Int =
        s.owners.filter { it.value == seat }.keys.count { s.level(it) == HOTEL_LEVEL }
}
