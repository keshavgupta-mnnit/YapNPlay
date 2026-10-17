package com.kglabs28.yapnplay.appengine.features.playroomlobby.domain

import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem

/**
 * Everything the lobby screen needs at one moment: the live room, who is
 * talking, who "me" is, the pickable games and the selected one.
 */
data class LobbyData(
    val room: LobbyRoom,
    val speakingUserIds: Set<String>,
    val myUserId: String,
    val games: List<GameCatalogItem>,
    val selectedGame: GameCatalogItem?,
)
