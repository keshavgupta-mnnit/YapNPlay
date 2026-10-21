package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.protocol.GuestCreated
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.kglabs28.yapnplay.appengine.protocol.PlayGamesSignInBody
import com.kglabs28.yapnplay.appengine.protocol.PlayGamesSignedIn
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * The signed-in account. The first time anything needs the backend, a guest
 * account is created (`POST /auth/guest`) and its secret saved on the
 * device; afterwards the same account is reused, also across restarts.
 * Google Play Games links it (or brings back the account linked before).
 */
class BackendSession(
    private val http: HttpClient,
    private val store: CredentialStore,
) {
    private val lock = Mutex()
    private val rejected = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /** The server refused the saved account (401): e.g. another phone restored it through Play Games. */
    val signInNeeded: SharedFlow<Unit> = rejected

    fun markRejected() {
        rejected.tryEmit(Unit)
    }

    /** The saved account, creating one first if this device has none. */
    suspend fun credentials(): BackendCredentials =
        store.load() ?: lock.withLock {
            store.load() ?: http.post(BackendPaths.GUEST).body<GuestCreated>()
                .let { BackendCredentials(it.userId, it.secret) }
                .also(store::save)
        }

    /**
     * Links this account to the Play Games player behind [serverAuthCode], or
     * switches to the account that player already has (saved for next time).
     * Returns true when the account changed (the app reloads).
     */
    suspend fun signInWithPlayGames(serverAuthCode: String): Boolean {
        val before = credentials().userId
        val reply = http.post(BackendPaths.PLAY_GAMES) {
            contentType(ContentType.Application.Json)
            setBody(PlayGamesSignInBody(serverAuthCode))
        }.body<PlayGamesSignedIn>()
        store.save(BackendCredentials(reply.userId, reply.secret))
        return reply.userId != before
    }

    /** This device's user id, once an account exists (null before the first call). */
    fun currentUserId(): String? = store.load()?.userId
}
