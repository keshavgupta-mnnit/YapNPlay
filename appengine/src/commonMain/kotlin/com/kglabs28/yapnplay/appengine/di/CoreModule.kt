package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.DefaultDispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.SystemAppClock
import com.kglabs28.yapnplay.appengine.core.common.createHttpClientEngine
import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.core.network.BackendSession
import com.kglabs28.yapnplay.appengine.core.network.SessionProvider
import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.core.network.RealtimeConnection
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import com.kglabs28.yapnplay.appengine.fake.FakeRealtimeConnection
import com.kglabs28.yapnplay.appengine.fake.LocalBackendSession
import com.kglabs28.yapnplay.appengine.fake.fakeHttpEngine
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

    // ── The transport. The only place that knows a server exists. ─────────
    // LOCAL_MODE (AppConstants) answers the app in-process, so the real
    // MONO engines can be played with no backend. Set it to false once
    // the backend hosts them and the real calls below are used again.

    // HTTP: the app's REST calls (profile, friends, rooms, online count).
    single {
        if (AppConstants.LOCAL_MODE) {
            HttpClient(fakeHttpEngine(get())) {
                expectSuccess = true
                install(ContentNegotiation) { json(get<Json>()) }
                install(DefaultRequest) { url(AppConstants.SERVER_BASE_URL) }
            }
        } else {
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
                        request.header(HttpHeaders.Authorization, BEARER + get<SessionProvider>().credentials().bearerToken)
                    }
                    execute(request)
                }
            }
        }
    }
    // The guest account. Local mode hands out a fixed one (no POST /auth/guest).
    single<SessionProvider> {
        if (AppConstants.LOCAL_MODE) LocalBackendSession() else BackendSession(get(), get())
    }
    // The realtime gateway: one WebSocket, or the in-process local host
    // that runs the real MONO engines and speaks the same envelopes.
    single<RealtimeGateway> {
        if (AppConstants.LOCAL_MODE) {
            FakeRealtimeConnection(get(), get(APP_SCOPE)).also { it.startTicker() }
        } else {
            RealtimeConnection(get(), get(), get(), get(APP_SCOPE))
        }
    }
    single { MatchChannel(get()) }
}

private const val BEARER = "Bearer "
private const val CONNECT_TIMEOUT_MS = 10_000L
