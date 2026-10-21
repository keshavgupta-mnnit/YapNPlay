package com.kglabs28.yapnplay.appengine.features.gamecatalog.data

import com.kglabs28.yapnplay.appengine.appmanager.helpers.GamesAppManagerHelper
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.features.gamecatalog.repository.GameCatalogRepository
import com.kglabs28.yapnplay.appengine.games.base.GameRegistry

/**
 * Catalog from the in-app [GameRegistry], filtered by remote flags, so a
 * game can be switched off without an app release. Games are code, not
 * server data: no network call here.
 */
class GameCatalogRepositoryImpl(
    private val registry: GameRegistry,
    private val gamesConfig: GamesAppManagerHelper,
) : GameCatalogRepository {

    override suspend fun getGames(): List<GameCatalogItem> =
        registry.games
            .filter { gamesConfig.isGameEnabled(it.id.value) }
            .map { it.toCatalogItem() }

    override suspend fun findGame(gameId: String): GameCatalogItem? =
        getGames().firstOrNull { it.id == gameId }
}
