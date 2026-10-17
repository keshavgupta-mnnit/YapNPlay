package com.kglabs28.yapnplay.appengine.features.gamecatalog.domain

import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRuleOption

/**
 * One game as shown in a game picker (Game Zone carousel/grid, room
 * lobby). Built from the game's own registration: name, three taglines,
 * art key, the player range allowed in rooms ([minPlayers]–[maxPlayers])
 * how many seats a random Quick Play match always fills, the house-rule
 * toggles and the fixed rule sheet shown in the room lobby.
 */
data class GameCatalogItem(
    val id: String,
    val name: String,
    val taglines: List<String>,
    val artKey: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val quickPlaySeats: Int,
    val houseRuleOptions: List<HouseRuleOption>,
    val fixedRules: List<String>,
)
