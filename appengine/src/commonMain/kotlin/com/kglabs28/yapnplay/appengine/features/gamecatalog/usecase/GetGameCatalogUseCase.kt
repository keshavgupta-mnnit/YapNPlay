package com.kglabs28.yapnplay.appengine.features.gamecatalog.usecase

import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.features.gamecatalog.repository.GameCatalogRepository

/**
 * The games to show in a picker, in registry order. Used by the Game
 * Zone today and the room lobby's game choice next.
 */
class GetGameCatalogUseCase(private val repository: GameCatalogRepository) {

    suspend operator fun invoke(): List<GameCatalogItem> = repository.getGames()
}
