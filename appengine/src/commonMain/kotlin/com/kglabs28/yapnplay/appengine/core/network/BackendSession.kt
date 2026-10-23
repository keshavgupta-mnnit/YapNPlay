package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.protocol.DeviceAccount
import com.kglabs28.yapnplay.appengine.protocol.DeviceAccountBody
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.CompletableDeferred
import io.ktor.http.HttpHeaders
import io.ktor.client.request.header
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import com.kglabs28.yapnplay.appengine.core.common.StartupGate
import com.kglabs28.yapnplay.appengine.protocol.CreateGuestBody
import kotlin.uuid.Uuid
import kotlin.uuid.ExperimentalUuidApi
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppLog
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
 * Where the app's bearer token comes from: a guest account created over
 * HTTP and cached on the device.
 */
interface SessionProvider {
    /** The account to authenticate with, creating one first if needed. */
    suspend fun credentials(): BackendCredentials

    /** This device's user id, once an account exists (null before the first call). */
    fun currentUserId(): String?
}

/**
 * The signed-in account. The first time anything needs the backend, a guest
 * account is created (`POST /auth/guest`) and its secret saved on the
 * device; afterwards the same account is reused, also across restarts. The
 * app picks the account's id and secret and saves them before asking, so a
 * crash before the reply is saved retries with the same account and never
 * makes another one. A first launch tries Play Games before making a guest
 * (its account, or a new one linked to it, needs none), and a guest is
 * sent with the phone's id so a reinstall gets it back.
 * Google Play Games links it (or brings back the account linked before).
 */
class BackendSession(
    private val http: HttpClient,
    private val store: CredentialStore,
    private val device: DeviceIdentity,
    private val startup: StartupGate,
) : SessionProvider {
    private val lock = Mutex()
    private val rejected = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val offers = MutableStateFlow<DeviceAccountOffer?>(null)
    private var answer: CompletableDeferred<Boolean>? = null

    /** An account already made on this phone, waiting for the player to continue it or start new. */
    val deviceAccountOffer: StateFlow<DeviceAccountOffer?> = offers.asStateFlow()

    /** The player's answer to [deviceAccountOffer]: continue that account, or start a new one. */
    fun answerDeviceAccount(continueIt: Boolean) {
        offers.value = null
        answer?.complete(continueIt)
    }

    /** The server refused the saved account (401): e.g. another phone restored it through Play Games. */
    val signInNeeded: SharedFlow<Unit> = rejected

    fun markRejected() {
        rejected.tryEmit(Unit)
    }

    /** The saved account, creating one first if this device has none. */
    override suspend fun credentials(): BackendCredentials {
        store.load()?.let { return it }
        // No account yet: Play Games goes first (it may bring one); a guest only if it brings none.
        withTimeoutOrNull(PLAY_GAMES_WAIT_MS) { startup.ready.first { it } }
        return store.load() ?: lock.withLock { store.load() ?: createGuest() }
    }

    /** An account already made on this phone: the player decides whether to continue it (no account: false). */
    private suspend fun continuesPhoneAccount(deviceId: String): Boolean {
        val found = runCatching {
            http.post(BackendPaths.DEVICE_ACCOUNT) {
                contentType(ContentType.Application.Json)
                setBody(DeviceAccountBody(deviceId))
            }.body<DeviceAccount>()
        }.getOrNull()?.takeIf { it.found } ?: return false
        val choice = CompletableDeferred<Boolean>().also { answer = it }
        offers.value = DeviceAccountOffer(found.displayName, found.avatarId, found.level)
        return choice.await()
    }

    /** The saved account's bearer, without making one (null: none saved yet). */
    fun savedBearer(): String? = store.load()?.bearerToken

    /** Signs up the account picked (and saved) first; the server's reply is what is kept. */
    @OptIn(ExperimentalUuidApi::class)
    private suspend fun createGuest(): BackendCredentials {
        val picked = store.loadPending()
            ?: BackendCredentials(Uuid.random().toString(), Uuid.random().toHexString() + Uuid.random().toHexString()).also(store::savePending)
        val deviceId = device.deviceId()
        val restore = deviceId != null && continuesPhoneAccount(deviceId)
        val response = http.post(BackendPaths.GUEST) {
            contentType(ContentType.Application.Json)
            setBody(CreateGuestBody(picked.userId, picked.secret, deviceId, restore))
        }
        // Refused (not just unreachable): the next try picks a fresh account.
        if (response.status.value in CLIENT_ERRORS) store.savePending(null)
        val created = response.body<GuestCreated>()
        return BackendCredentials(created.userId, created.secret).also {
            store.save(it)
            store.savePending(null)
        }
    }

    /**
     * Links this account to the Play Games player behind [serverAuthCode], or
     * switches to the account that player already has (saved for next time).
     * Returns true when the account changed (the app reloads).
     */
    suspend fun signInWithPlayGames(serverAuthCode: String): Boolean {
        // The account saved so far, if any: never a new guest just to send along.
        val before = store.load()
        val response = http.post(BackendPaths.PLAY_GAMES) {
            contentType(ContentType.Application.Json)
            before?.let { header(HttpHeaders.Authorization, BEARER + it.bearerToken) }
            setBody(PlayGamesSignInBody(serverAuthCode))
        }
        if (!response.status.isSuccess()) {
            AppLog.d(AppConstants.LOG_TAG_AUTH, "server: refused HTTP ${response.status.value} ${response.bodyAsText().take(ERROR_LOG_MAX)}")
        }
        val reply = response.body<PlayGamesSignedIn>()
        store.save(BackendCredentials(reply.userId, reply.secret))
        // Only a switch from one saved account to another reloads the app; a first sign-in just goes on.
        val switched = before != null && reply.userId != before.userId
        AppLog.d(AppConstants.LOG_TAG_AUTH, "server: ${if (switched) "restored the linked account (app reloads)" else "signed in with Play Games"}")
        return switched
    }

    /** This device's user id, once an account exists (null before the first call). */
    override fun currentUserId(): String? = store.load()?.userId
}

/** How long a first launch waits for Play Games before making a guest (it may have no Play Games at all). */
private const val PLAY_GAMES_WAIT_MS = 15_000L

private const val BEARER = "Bearer "

/** A reply in this range means the server refused the picked account. */
private val CLIENT_ERRORS = 400..499

/** How much of a refused sign-in reply goes in the debug log. */
private const val ERROR_LOG_MAX = 300
