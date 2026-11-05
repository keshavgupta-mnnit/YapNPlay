package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.features.nearby.usecase.GetNearbyRadiosUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.presentation.NearbyReadyChecklist
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.OpenNearbyRadioSettingsUseCase
import com.kglabs28.yapnplay.appengine.core.network.OfflineMode
import com.kglabs28.yapnplay.appengine.features.nearby.data.NearbyFrames
import com.kglabs28.yapnplay.appengine.features.nearby.data.NearbyIdentity
import com.kglabs28.yapnplay.appengine.features.nearby.data.NearbyRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.nearby.data.OfflineRealtimeGateway
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostClock
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostEnvelopes
import com.kglabs28.yapnplay.appengine.features.nearby.host.games.citybazaar.CityBazaarSessionFactory
import com.kglabs28.yapnplay.appengine.features.nearby.host.games.doodleking.DoodleKingSessionFactory
import com.kglabs28.yapnplay.appengine.features.nearby.host.games.mono.MonoChaosSessionFactory
import com.kglabs28.yapnplay.appengine.features.nearby.host.games.mono.MonoSessionFactory
import com.kglabs28.yapnplay.appengine.features.nearby.host.games.royalludo.RoyalLudoSessionFactory
import com.kglabs28.yapnplay.appengine.features.nearby.host.match.HostMatchChannel
import com.kglabs28.yapnplay.appengine.features.nearby.host.match.HostMatchStarter
import com.kglabs28.yapnplay.appengine.features.nearby.host.settings.Settings
import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.ClearNearbyProblemUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.IsNearbySupportedUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.GetMyPlayerIdUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.AcceptNearbyInviteUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.DeclineNearbyInviteUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.HostNearbyPartyUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.InviteNearbyPlayerUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.IsNearbyRoomUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.SetNearbyFindableUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.LeaveNearbyUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.ObserveNearbyUseCase
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import com.kglabs28.yapnplay.appengine.games.citybazaar.engine.CityBazaarEngine
import com.kglabs28.yapnplay.appengine.games.doodleking.DoodleKingEngine
import com.kglabs28.yapnplay.appengine.games.mono.engine.MonoEngine
import com.kglabs28.yapnplay.appengine.games.monochaos.engine.MonoChaosEngine
import com.kglabs28.yapnplay.appengine.games.royalludo.engine.RoyalLudoEngine
import com.kglabs28.yapnplay.appengine.core.common.AppClock
import org.koin.dsl.module

/**
 * Offline Nearby play: the phone-to-phone link (NearbyTransport comes from
 * the platform module), the offline realtime gateway, and the host that
 * runs every game on a phone with the backend's own rules.
 */
val nearbyModule = module {
    single { OfflineMode() }
    single { OfflineRealtimeGateway(get()) }
    single { HostEnvelopes(get()) }
    single { val clock = get<AppClock>(); HostClock { clock.now().toEpochMilliseconds() } }
    single<Settings> { Settings.Defaults }
    single {
        val random = get<GameRandom>()
        val clock = get<HostClock>()
        val json = get<HostEnvelopes>().json
        val settings = get<Settings>()
        HostMatchStarter(
            listOf(
                RoyalLudoSessionFactory(RoyalLudoEngine(), random, json, settings),
                CityBazaarSessionFactory(CityBazaarEngine(), random, json, clock, settings),
                MonoSessionFactory(MonoEngine(), random, json, clock, settings),
                MonoChaosSessionFactory(MonoChaosEngine(), random, json, clock, settings),
                DoodleKingSessionFactory(DoodleKingEngine(), random, json, clock, settings),
            ),
            clock,
            settings,
            get(),
        )
    }
    single { HostMatchChannel(get()) }
    single { NearbyFrames(get()) }
    // One per app run: a phone with no account keeps the same stand-in id all along.
    single { NearbyIdentity(get(), get(), get()) }
    factory { GetMyPlayerIdUseCase(get()) }
    single<NearbyRepository> { NearbyRepositoryImpl(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(APP_SCOPE)) }
    factory { ObserveNearbyUseCase(get()) }
    factory { IsNearbySupportedUseCase(get()) }
    factory { HostNearbyPartyUseCase(get()) }
    factory { GetNearbyRadiosUseCase(get()) }
    factory { NearbyReadyChecklist(get(), get()) }
    factory { OpenNearbyRadioSettingsUseCase(get()) }
    factory { InviteNearbyPlayerUseCase(get()) }
    factory { AcceptNearbyInviteUseCase(get()) }
    factory { DeclineNearbyInviteUseCase(get()) }
    factory { SetNearbyFindableUseCase(get()) }
    factory { IsNearbyRoomUseCase(get()) }
    factory { LeaveNearbyUseCase(get()) }
    factory { ClearNearbyProblemUseCase(get()) }
}
