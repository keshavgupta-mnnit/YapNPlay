package com.kglabs28.yapnplay.appengine.games.citybazaar.utils

/**
 * The 36 tiles, clockwise from START (docs/city-bazaar-rules.md §2): the
 * spec's board, with the original game's card values where they are known
 * (Chandigarh, Darjeeling, Delhi, Mumbai, Amritsar, Railways).
 */
object CityBazaarBoard {
    val tiles: List<TileSpec> = listOf(
        TileSpec(0, "START", TileKind.START),
        TileSpec(1, "Cochin", TileKind.CITY, CityGroup.RED, 3000, listOf(400, 1200, 2400, 3600, 5000), 1500),
        TileSpec(2, "Internet", TileKind.UTILITY, CityGroup.PURPLE, 6000, listOf(800)),
        TileSpec(3, "Railways", TileKind.TRANSIT, CityGroup.ORANGE, 9500, listOf(1000)),
        TileSpec(4, "Chandigarh", TileKind.CITY, CityGroup.RED, 2500, listOf(200, 900, 1600, 2500, 3500), 3000),
        TileSpec(5, "Income Tax", TileKind.TAX, tax = 1000),
        TileSpec(6, "Indore", TileKind.CITY, CityGroup.BLUE, 1500, listOf(200, 600, 1200, 1800, 2500), 1000),
        TileSpec(7, "Luck", TileKind.LUCK),
        TileSpec(8, "Jaipur", TileKind.CITY, CityGroup.BLUE, 3000, listOf(400, 1200, 2400, 3600, 5000), 1500),
        TileSpec(9, "Jail", TileKind.JAIL),
        TileSpec(10, "Delhi", TileKind.CITY, CityGroup.GREEN, 6000, listOf(750, 3000, 4300, 5500, 7500), 5000),
        TileSpec(11, "Ahmedabad", TileKind.CITY, CityGroup.GREEN, 3500, listOf(450, 1400, 2800, 4200, 5500), 1500),
        TileSpec(12, "Electricity", TileKind.UTILITY, CityGroup.PURPLE, 3500, listOf(500)),
        TileSpec(13, "Roadways", TileKind.TRANSIT, CityGroup.ORANGE, 3500, listOf(500)),
        TileSpec(14, "Amritsar", TileKind.CITY, CityGroup.YELLOW, 3300, listOf(300, 1350, 2400, 3750, 5250), 3000),
        TileSpec(15, "Goa", TileKind.CITY, CityGroup.YELLOW, 4000, listOf(500, 2000, 3500, 5000, 6500), 1500),
        TileSpec(16, "Shagun Chest", TileKind.CHEST),
        TileSpec(17, "Hyderabad", TileKind.CITY, CityGroup.YELLOW, 3500, listOf(450, 1400, 2800, 4200, 5500), 1500),
        TileSpec(18, "Club", TileKind.CLUB),
        TileSpec(19, "Agra", TileKind.CITY, CityGroup.BLUE, 2500, listOf(350, 1000, 2000, 3000, 4000), 1000),
        TileSpec(20, "Luck", TileKind.LUCK),
        TileSpec(21, "Kanpur", TileKind.CITY, CityGroup.BLUE, 4000, listOf(500, 1500, 3000, 4500, 6000), 2000),
        TileSpec(22, "Patna", TileKind.CITY, CityGroup.BLUE, 2000, listOf(250, 800, 1600, 2400, 3200), 1000),
        TileSpec(23, "Darjeeling", TileKind.CITY, CityGroup.RED, 2500, listOf(200, 1200, 2600, 3500, 5000), 3000),
        TileSpec(24, "Airways", TileKind.TRANSIT, CityGroup.ORANGE, 11000, listOf(1500)),
        TileSpec(25, "Ladakh", TileKind.CITY, CityGroup.RED, 6500, listOf(850, 2400, 4800, 7200, 9500), 3000),
        TileSpec(26, "Shimla", TileKind.CITY, CityGroup.RED, 3500, listOf(450, 1400, 2800, 4200, 5500), 1500),
        TileSpec(27, "Rest House", TileKind.REST_HOUSE),
        TileSpec(28, "Chennai", TileKind.CITY, CityGroup.YELLOW, 7000, listOf(900, 2600, 5200, 7800, 10000), 3500),
        TileSpec(29, "Shagun Chest", TileKind.CHEST),
        TileSpec(30, "Bengaluru", TileKind.CITY, CityGroup.YELLOW, 5000, listOf(650, 1900, 3800, 5700, 7500), 2500),
        TileSpec(31, "Wealth Tax", TileKind.TAX, taxPerBuilding = WEALTH_TAX_PER_BUILDING),
        TileSpec(32, "Pune", TileKind.CITY, CityGroup.GREEN, 3000, listOf(400, 1200, 2400, 3600, 5000), 1500),
        TileSpec(33, "Kolkata", TileKind.CITY, CityGroup.GREEN, 4000, listOf(500, 1500, 3000, 4500, 6000), 2000),
        TileSpec(34, "Waterways", TileKind.TRANSIT, CityGroup.ORANGE, 3000, listOf(400)),
        TileSpec(35, "Mumbai", TileKind.CITY, CityGroup.GREEN, 8500, listOf(1200, 4000, 5700, 7300, 10000), 7000),
    )

    fun tile(index: Int): TileSpec = tiles[index]

    fun groupTiles(group: CityGroup): List<Int> = tiles.filter { it.group == group }.map { it.index }

    fun tilesOf(kind: TileKind): List<Int> = tiles.filter { it.kind == kind }.map { it.index }
}
