package com.kglabs28.yapnplay.appengine.games.citybaazar.utils

/**
 * The 36 tiles, clockwise from START (docs/city-baazar-rules.md §2).
 * Shared by the engine and the table drawing.
 */
object CityBaazarBoard {
    val tiles: List<TileSpec> = listOf(
        TileSpec(0, "START", TileKind.START),
        TileSpec(1, "Indore", TileKind.CITY, CityGroup.BROWN, 1500, listOf(200, 600, 1200, 1800, 2500), 1000),
        TileSpec(2, "Internet", TileKind.UTILITY, price = 3500),
        TileSpec(3, "Railways", TileKind.TRANSIT, price = 4000),
        TileSpec(4, "Patna", TileKind.CITY, CityGroup.BROWN, 2000, listOf(250, 800, 1600, 2400, 3200), 1000),
        TileSpec(5, "Income Tax", TileKind.TAX, tax = 1000),
        TileSpec(6, "Agra", TileKind.CITY, CityGroup.SKY, 2500, listOf(350, 1000, 2000, 3000, 4000), 1000),
        TileSpec(7, "Luck", TileKind.LUCK),
        TileSpec(8, "Darjeeling", TileKind.CITY, CityGroup.SKY, 2500, listOf(350, 1000, 2000, 3000, 4000), 1000),
        TileSpec(9, "Jail", TileKind.JAIL),
        TileSpec(10, "Cochin", TileKind.CITY, CityGroup.PINK, 3000, listOf(400, 1200, 2400, 3600, 5000), 1500),
        TileSpec(11, "Jaipur", TileKind.CITY, CityGroup.PINK, 3000, listOf(400, 1200, 2400, 3600, 5000), 1500),
        TileSpec(12, "Electricity", TileKind.UTILITY, price = 3500),
        TileSpec(13, "Roadways", TileKind.TRANSIT, price = 4000),
        TileSpec(14, "Pune", TileKind.CITY, CityGroup.AMBER, 3000, listOf(400, 1200, 2400, 3600, 5000), 1500),
        TileSpec(15, "Ahmedabad", TileKind.CITY, CityGroup.AMBER, 3500, listOf(450, 1400, 2800, 4200, 5500), 1500),
        TileSpec(16, "Shagun Chest", TileKind.CHEST),
        TileSpec(17, "Hyderabad", TileKind.CITY, CityGroup.AMBER, 3500, listOf(450, 1400, 2800, 4200, 5500), 1500),
        TileSpec(18, "Club", TileKind.CLUB),
        TileSpec(19, "Shimla", TileKind.CITY, CityGroup.CRIMSON, 3500, listOf(450, 1400, 2800, 4200, 5500), 1500),
        TileSpec(20, "Luck", TileKind.LUCK),
        TileSpec(21, "Chandigarh", TileKind.CITY, CityGroup.CRIMSON, 4000, listOf(500, 1500, 3000, 4500, 6000), 2000),
        TileSpec(22, "Goa", TileKind.CITY, CityGroup.CRIMSON, 4000, listOf(500, 1500, 3000, 4500, 6000), 2000),
        TileSpec(23, "Kanpur", TileKind.CITY, CityGroup.LIME, 4000, listOf(500, 1500, 3000, 4500, 6000), 2000),
        TileSpec(24, "Airways", TileKind.TRANSIT, price = 4000),
        TileSpec(25, "Kolkata", TileKind.CITY, CityGroup.LIME, 4000, listOf(500, 1500, 3000, 4500, 6000), 2000),
        TileSpec(26, "Amritsar", TileKind.CITY, CityGroup.LIME, 4500, listOf(600, 1800, 3600, 5400, 7000), 2000),
        TileSpec(27, "Rest House", TileKind.REST_HOUSE),
        TileSpec(28, "Bengaluru", TileKind.CITY, CityGroup.TEAL, 5000, listOf(650, 1900, 3800, 5700, 7500), 2500),
        TileSpec(29, "Shagun Chest", TileKind.CHEST),
        TileSpec(30, "Delhi", TileKind.CITY, CityGroup.TEAL, 6000, listOf(800, 2200, 4400, 6500, 8500), 3000),
        TileSpec(31, "Wealth Tax", TileKind.TAX, tax = 1500),
        TileSpec(32, "Ladakh", TileKind.CITY, CityGroup.INDIGO, 6500, listOf(850, 2400, 4800, 7200, 9500), 3000),
        TileSpec(33, "Chennai", TileKind.CITY, CityGroup.INDIGO, 7000, listOf(900, 2600, 5200, 7800, 10000), 3500),
        TileSpec(34, "Waterways", TileKind.TRANSIT, price = 4000),
        TileSpec(35, "Mumbai", TileKind.CITY, CityGroup.INDIGO, 8000, listOf(1100, 3200, 6400, 9600, 12500), 4000),
    )

    fun tile(index: Int): TileSpec = tiles[index]

    fun groupTiles(group: CityGroup): List<Int> = tiles.filter { it.group == group }.map { it.index }

    fun tilesOf(kind: TileKind): List<Int> = tiles.filter { it.kind == kind }.map { it.index }
}
