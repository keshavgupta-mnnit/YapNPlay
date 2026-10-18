package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.DefaultDispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.SystemAppClock
import com.kglabs28.yapnplay.appengine.core.common.createHttpClientEngine
import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.core.network.BackendSession
import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.core.network.RealtimeConnection
import com.kglabs28.yapnplay.appengine.games.base.play.DefaultGameRandom
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

/** Scope of app-wide background work (the WebSocket). */
val APP_SCOPE = named("app-scope")

/**
 * App-wide singletons: threads, clock, randomness, and the backend
 * connection (HTTP with the guest's bearer token, one WebSocket).
 */
val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<AppClock> { SystemAppClock() }
    single<GameRandom> { DefaultGameRandom() }
    // Contract JSON: unknown fields ignored, defaults written.
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }
    single<CoroutineScope>(APP_SCOPE) { CoroutineScope(SupervisorJob() + get<DispatcherProvider>().io) }
    single {
        HttpClient(createHttpClientEngine()) {
            expectSuccess = true
            install(ContentNegotiation) { json(get<Json>()) }
            install(DefaultRequest) { url(AppConstants.SERVER_BASE_URL) }
            // An unreachable backend fails fast instead of leaving screens empty.
            install(HttpTimeout) { connectTimeoutMillis = CONNECT_TIMEOUT_MS }
            install(WebSockets)
        }.also { client ->
            // Every call except guest sign-up carries "Bearer <userId>.<secret>".
            client.plugin(HttpSend).intercept { request ->
                val path = request.url.buildString()
                if (!path.endsWith(BackendPaths.GUEST) && request.headers[HttpHeaders.Authorization] == null) {
                    request.header(HttpHeaders.Authorization, BEARER + get<BackendSession>().credentials().bearerToken)
                }
                execute(request)
            }
        }
    }
    single { BackendSession(get(), get()) }
    single { RealtimeConnection(get(), get(), get(), get(APP_SCOPE)) }
    single { MatchChannel(get()) }
}

private const val BEARER = "Bearer "
private const val CONNECT_TIMEOUT_MS = 10_000L
