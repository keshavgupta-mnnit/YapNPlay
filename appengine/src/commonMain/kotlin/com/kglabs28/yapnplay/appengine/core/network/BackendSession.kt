package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.protocol.GuestCreated
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Where the app's bearer token comes from. The real one creates a guest
 * account over HTTP and caches it on the device; local mode hands out a
 * fixed account so the app runs with no server.
 */
interface SessionProvider {
    /** The account to authenticate with, creating one first if needed. */
    suspend fun credentials(): BackendCredentials

    /** This device's user id, once an account exists (null before the first call). */
    fun currentUserId(): String?
}

/**
 * The signed-in guest. The first time anything needs the backend, a guest
 * account is created (`POST /auth/guest`) and its secret saved on the
 * device; afterwards the same account is reused, also across restarts.
 */
class BackendSession(
    private val http: HttpClient,
    private val store: CredentialStore,
) : SessionProvider {
    private val lock = Mutex()

    /** The saved account, creating one first if this device has none. */
    override suspend fun credentials(): BackendCredentials =
        store.load() ?: lock.withLock {
            store.load() ?: http.post(BackendPaths.GUEST).body<GuestCreated>()
                .let { BackendCredentials(it.userId, it.secret) }
                .also(store::save)
        }

    /** This device's user id, once an account exists (null before the first call). */
    override fun currentUserId(): String? = store.load()?.userId
}