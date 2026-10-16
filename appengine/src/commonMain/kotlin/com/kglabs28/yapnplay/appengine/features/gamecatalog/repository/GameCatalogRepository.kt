package com.kglabs28.yapnplay.appengine.features.gamecatalog.repository

import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem

/**
 * The games a user can pick right now: every registered game that its
 * remote flag allows. Shared by the Game Zone and the room lobby.
 */
interface GameCatalogRepository {
    suspend fun getGames(): List<GameCatalogItem>
    suspend fun findGame(gameId: String): GameCatalogItem?
}
