package com.kglabs28.yapnplay.appengine.features.gamecatalog.data

import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.features.gamecatalog.repository.GameCatalogRepository
import com.kglabs28.yapnplay.appengine.games.base.GameRegistry

/**
 * Catalog from the in-app [GameRegistry], filtered by remote flags, so a
 * game can be switched off without an app release. Games are code, not
 * server data: no network call here.
 *
 * TEMPORARY: the placeholder games from [FakeDataProvider.Games] are
 * appended until their engines are registered — delete that line then.
 */
class GameCatalogRepositoryImpl(
    private val registry: GameRegistry,
    private val appManager: AppManager,
) : GameCatalogRepository {

    override suspend fun getGames(): List<GameCatalogItem> =
        registry.games
            .filter { appManager.isGameEnabled(it.id.value) }
            .map { it.toCatalogItem() } +
            FakeDataProvider.Games.placeholders

    override suspend fun findGame(gameId: String): GameCatalogItem? =
        getGames().firstOrNull { it.id == gameId }
}
