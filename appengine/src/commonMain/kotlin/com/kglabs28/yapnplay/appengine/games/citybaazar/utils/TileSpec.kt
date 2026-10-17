package com.kglabs28.yapnplay.appengine.games.citybaazar.utils

/**
 * One board tile. [rents] for a city: land, 1, 2, 3 houses, hotel.
 * [price] is the purchase price (cities, transit, utilities); [tax] what a
 * tax tile charges.
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
) {
    val buyable: Boolean get() = kind == TileKind.CITY || kind == TileKind.TRANSIT || kind == TileKind.UTILITY
}
