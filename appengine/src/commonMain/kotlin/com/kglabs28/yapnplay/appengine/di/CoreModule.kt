package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DefaultDispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.SystemAppClock
import com.kglabs28.yapnplay.appengine.games.base.play.DefaultGameRandom
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import org.koin.dsl.module

/** App-wide singletons: threads, clock, randomness. */
val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<AppClock> { SystemAppClock() }
    single<GameRandom> { DefaultGameRandom() }
}
