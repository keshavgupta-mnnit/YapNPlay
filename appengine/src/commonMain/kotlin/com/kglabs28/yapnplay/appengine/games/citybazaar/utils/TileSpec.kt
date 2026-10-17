package com.kglabs28.yapnplay.appengine.games.citybazaar.utils

/**
 * One board tile. [rents] for a city: land, 1, 2, 3 houses, hotel; for
 * transit and utilities a single fixed rent. [buildCost] is the price of
 * one house, and of the hotel. [tax] is what a tax tile charges, plus
 * [taxPerBuilding] for each building the player owns (Wealth Tax).
 */
data class TileSpec(
    val index: Int,
    val name: String,
    val kind: TileKind,
    val group: CityGroup? = null,
    val price: Int = 0,
    val rents: List<Int> = emptyList(),
    val buildCost: Int = 0,
    val tax: Int = 0,
    val taxPerBuilding: Int = 0,
) {
    val buyable: Boolean get() = kind == TileKind.CITY || kind == TileKind.TRANSIT || kind == TileKind.UTILITY
}
