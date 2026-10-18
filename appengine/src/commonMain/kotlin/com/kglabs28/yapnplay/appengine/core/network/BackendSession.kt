package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.protocol.GuestCreated
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * The signed-in guest. The first time anything needs the backend, a guest
 * account is created (`POST /auth/guest`) and its secret saved on the
 * device; afterwards the same account is reused, also across restarts.
 */
class BackendSession(
    private val http: HttpClient,
    private val store: CredentialStore,
) {
    private val lock = Mutex()

    /** The saved account, creating one first if this device has none. */
    suspend fun credentials(): BackendCredentials =
        store.load() ?: lock.withLock {
            store.load() ?: http.post(BackendPaths.GUEST).body<GuestCreated>()
                .let { BackendCredentials(it.userId, it.secret) }
                .also(store::save)
        }

    /** This device's user id, once an account exists (null before the first call). */
    fun currentUserId(): String? = store.load()?.userId
}
