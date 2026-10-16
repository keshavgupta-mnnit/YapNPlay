package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DefaultDispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.SystemAppClock
import com.kglabs28.yapnplay.appengine.core.common.createHttpClientEngine
import com.kglabs28.yapnplay.appengine.games.base.play.DefaultGameRandom
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/** App-wide singletons: threads, clock, randomness, HTTP client. */
val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<AppClock> { SystemAppClock() }
    single<GameRandom> { DefaultGameRandom() }
    single {
        HttpClient(createHttpClientEngine()) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }
}
