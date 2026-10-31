package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.connection.usecase.ObserveConnectionQualityUseCase
import com.kglabs28.yapnplay.appengine.core.network.PingProbe
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.GetMatchRoomUseCase
import com.kglabs28.yapnplay.appengine.features.seathandover.usecase.SendSeatCommandUseCase
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverController
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatHandoverRepository
import com.kglabs28.yapnplay.appengine.features.seathandover.data.SeatHandoverRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.gamecatalog.data.GameCatalogRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.gamecatalog.repository.GameCatalogRepository
import com.kglabs28.yapnplay.appengine.features.gamecatalog.usecase.GetGameCatalogUseCase
import com.kglabs28.yapnplay.appengine.features.matchmaking.data.MatchmakingRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.matchmaking.data.remote.MatchmakingClient
import com.kglabs28.yapnplay.appengine.features.matchmaking.data.remote.MatchmakingClientImpl
import com.kglabs28.yapnplay.appengine.features.matchmaking.repository.MatchmakingRepository
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.FindQuickPlayMatchUseCase
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.GetActiveMatchUseCase
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.GetOnlinePlayerCountUseCase
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.LeaveQuickPlayQueueUseCase
import com.kglabs28.yapnplay.appengine.games.base.GameRegistry
import com.kglabs28.yapnplay.appengine.games.citybazaar.engine.CityBazaarEngine
import com.kglabs28.yapnplay.appengine.games.mono.engine.MonoEngine
import com.kglabs28.yapnplay.appengine.games.monochaos.engine.MonoChaosEngine
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.RoyalLudoEngine
import org.koin.dsl.module

/**
 * Games and Quick Play. A new game registers itself by adding its
 * engine to the [GameRegistry] list below — nothing else changes.
 */
val gamesModule = module {
    // Seat handover, the same in every game.
    single<SeatHandoverRepository> { SeatHandoverRepositoryImpl(get(), get()) }
    factory { SendSeatCommandUseCase(get()) }
    factory { SeatHandoverController(get()) }
    factory { GetMatchRoomUseCase(get()) }
    // The connection badge on every table.
    single { PingProbe(get()) }
    factory { ObserveConnectionQualityUseCase(get()) }
    single {
        GameRegistry(
            listOf(
                RoyalLudoEngine(),
                CityBazaarEngine(),
                MonoEngine(),
                MonoChaosEngine(),
            ),
        )
    }
    single<GameCatalogRepository> { GameCatalogRepositoryImpl(get(), get()) }
    factory { GetGameCatalogUseCase(get()) }

    single<MatchmakingClient> { MatchmakingClientImpl(get(), get()) }
    single<MatchmakingRepository> { MatchmakingRepositoryImpl(get(), get(), get()) }
    factory { FindQuickPlayMatchUseCase(get()) }
    factory { LeaveQuickPlayQueueUseCase(get()) }
    factory { GetOnlinePlayerCountUseCase(get()) }
    factory { GetActiveMatchUseCase(get()) }
}
