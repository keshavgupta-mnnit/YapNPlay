package com.kglabs28.yapnplay.appengine.games.citybaazar.engine

import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoard
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.HOTEL_LEVEL
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.RESALE_DIVISOR
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.SET_FOR_DOUBLE
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TileKind

/** Rents, building rights and values. Pure functions over the state. */
object CityBaazarEconomy {

    /** How many tiles of [tile]'s colour [seat] owns. */
    fun colourCount(s: CityBaazarState, seat: Int, tile: Int): Int {
        val group = CityBaazarBoard.tile(tile).group ?: return 0
        return CityBaazarBoard.groupTiles(group).count { s.owners[it] == seat }
    }

    /** 3 (or more) of a colour: that colour's rent doubles, whatever it is. */
    fun doubled(s: CityBaazarState, owner: Int, tile: Int): Boolean =
        s.cbRules.house.setDoubleRent && colourCount(s, owner, tile) >= SET_FOR_DOUBLE

    /** [seat] may build on [tile] (their city, not yet a hotel), ignoring money. */
    fun buildAllowed(s: CityBaazarState, seat: Int, tile: Int): Boolean =
        CityBaazarBoard.tile(tile).kind == TileKind.CITY && s.owners[tile] == seat && s.level(tile) < HOTEL_LEVEL

    /** Rent for landing on [tile] now: its tier's rent, doubled with 3 of a colour, halved while the owner sits it out in jail. */
    fun rent(s: CityBaazarState, tile: Int): Int {
        val owner = s.owners[tile] ?: return 0
        val spec = CityBaazarBoard.tile(tile)
        val base = spec.rents.getOrElse(if (spec.kind == TileKind.CITY) s.level(tile) else 0) { 0 }
        val withColour = if (doubled(s, owner, tile)) base * 2 else base
        return if (owner in s.halfRent) withColour / 2 else withColour
    }

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

    fun properties(s: CityBaazarState, seat: Int): Int = s.owners.count { it.value == seat }

    /** Each house and each hotel counts as one building. */
    fun buildings(s: CityBaazarState, seat: Int): Int = houses(s, seat) + hotels(s, seat)

    fun houses(s: CityBaazarState, seat: Int): Int =
        s.owners.filter { it.value == seat }.keys.sumOf { s.level(it).let { l -> if (l in 1 until HOTEL_LEVEL) l else 0 } }

    fun hotels(s: CityBaazarState, seat: Int): Int =
        s.owners.filter { it.value == seat }.keys.count { s.level(it) == HOTEL_LEVEL }
}
