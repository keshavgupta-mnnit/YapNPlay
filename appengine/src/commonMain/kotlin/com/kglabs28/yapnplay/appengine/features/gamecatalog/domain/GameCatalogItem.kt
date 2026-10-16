package com.kglabs28.yapnplay.appengine.features.gamecatalog.domain

/**
 * One game as shown in a game picker (Game Zone carousel/grid, room
 * lobby). Built from the game's own registration: name, three taglines,
 * art key, the player range allowed in rooms ([minPlayers]–[maxPlayers])
 * and how many seats a random Quick Play match always fills.
 */
data class GameCatalogItem(
    val id: String,
    val name: String,
    val taglines: List<String>,
    val artKey: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val quickPlaySeats: Int,
)
