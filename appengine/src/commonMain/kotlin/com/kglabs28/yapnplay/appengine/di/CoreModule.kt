package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.games.base.GameRegistry
import io.ktor.http.HttpStatusCode
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AppManagerHelper
import com.kglabs28.yapnplay.appengine.core.common.AppClock
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
    // HTTP: the app's REST calls (profile, friends, rooms, online count).
    single {
        val config = get<AppManagerHelper>()
        HttpClient(createHttpClientEngine()) {
            expectSuccess = true
            install(ContentNegotiation) { json(get<Json>()) }
            // Read per request: Remote Config (AppManager) can move the backend.
            install(DefaultRequest) { url(config.serverBaseUrl()) }
            // An unreachable backend fails fast instead of leaving screens empty.
            install(HttpTimeout) { connectTimeoutMillis = CONNECT_TIMEOUT_MS }
            install(WebSockets)
        }.also { client ->
            // Every call carries "Bearer <userId>.<secret>" except sign-up: guest, the phone's account
            // lookup and Play Games (which sends the saved one itself, never makes a guest just to send).
            client.plugin(HttpSend).intercept { request ->
                val path = request.url.buildString()
                val signUp = path.endsWith(BackendPaths.GUEST) || path.endsWith(BackendPaths.PLAY_GAMES) || path.endsWith(BackendPaths.DEVICE_ACCOUNT)
                if (!signUp && request.headers[HttpHeaders.Authorization] == null) {
                    request.header(
                        HttpHeaders.Authorization,
                        BEARER + get<BackendSession>().credentials().bearerToken
                    )
                }
                execute(request).also { call ->
                    // The saved account was refused (restored on another phone): sign in with Play Games again.
                    if (call.response.status == HttpStatusCode.Unauthorized && !path.endsWith(
                            BackendPaths.PLAY_GAMES
                        )
                    ) {
                        get<BackendSession>().markRejected()
                    }
                }
            }
        }
    }
    single { BackendSession(get(), get(), get(), get()) }
    // The account the app authenticates with.
    single<SessionProvider> { get<BackendSession>() }
    // The socket: the real-time match connection to the backend.
    single<RealtimeGateway> {
        val config = get<AppManagerHelper>()
        RealtimeConnection(get(), get(), get(), get(APP_SCOPE), baseUrl = { config.serverBaseUrl() }, games = { get<GameRegistry>().games.map { it.id.value } })
    }
    single { MatchChannel(get()) }
}

private const val BEARER = "Bearer "
private const val CONNECT_TIMEOUT_MS = 10_000L
