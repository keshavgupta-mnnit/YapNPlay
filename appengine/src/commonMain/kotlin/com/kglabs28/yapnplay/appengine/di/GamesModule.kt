package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.gamecatalog.data.GameCatalogRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.gamecatalog.repository.GameCatalogRepository
import com.kglabs28.yapnplay.appengine.features.gamecatalog.usecase.GetGameCatalogUseCase
import com.kglabs28.yapnplay.appengine.features.matchmaking.data.MatchmakingRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.matchmaking.data.remote.MatchmakingClient
import com.kglabs28.yapnplay.appengine.features.matchmaking.data.remote.MatchmakingClientImpl
import com.kglabs28.yapnplay.appengine.features.matchmaking.repository.MatchmakingRepository
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.FindQuickPlayMatchUseCase
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.GetOnlinePlayerCountUseCase
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.LeaveQuickPlayQueueUseCase
import com.kglabs28.yapnplay.appengine.games.base.GameRegistry
import com.kglabs28.yapnplay.appengine.games.citybazaar.engine.CityBazaarEngine
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.RoyalLudoEngine
import org.koin.dsl.module

/**
 * Games and Quick Play. A new game registers itself by adding its
 * engine to the [GameRegistry] list below — nothing else changes.
 */
val gamesModule = module {
    single {
        GameRegistry(
            listOf(
                RoyalLudoEngine(),
                CityBazaarEngine(),
            ),
        )
    }
    single<GameCatalogRepository> { GameCatalogRepositoryImpl(get(), get()) }
    factory { GetGameCatalogUseCase(get()) }

    single<MatchmakingClient> { MatchmakingClientImpl(get(), get(), get()) }
    single<MatchmakingRepository> { MatchmakingRepositoryImpl(get(), get(), get()) }
    factory { FindQuickPlayMatchUseCase(get()) }
    factory { LeaveQuickPlayQueueUseCase(get()) }
    factory { GetOnlinePlayerCountUseCase(get()) }
}
