package com.kglabs28.yapnplay.appengine.fake

import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.protocol.ActiveMatch
import com.kglabs28.yapnplay.appengine.protocol.BlockedUsers
import com.kglabs28.yapnplay.appengine.protocol.FriendRequestOutcomeCode
import com.kglabs28.yapnplay.appengine.protocol.FriendRequests
import com.kglabs28.yapnplay.appengine.protocol.Friends
import com.kglabs28.yapnplay.appengine.protocol.GuestCreated
import com.kglabs28.yapnplay.appengine.protocol.Me
import com.kglabs28.yapnplay.appengine.protocol.MyRooms
import com.kglabs28.yapnplay.appengine.protocol.OnlineStats
import com.kglabs28.yapnplay.appengine.protocol.RoomPreview
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestReply
import com.kglabs28.yapnplay.appengine.protocol.VoiceToken
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.serialization.json.Json

/**
 * A Ktor engine that answers every backend path in-process, so the app
 * runs with no server at all. It sits exactly where the real transport
 * sits — the lowest network layer — so nothing above it changes: the
 * same `HttpClient`, the same API clients, the same JSON.
 *
 * Only what the app actually calls is answered. Rooms, friends and
 * moderation get small believable payloads so those screens render
 * instead of erroring; the two MONO games get their real matches from
 * [FakeRealtimeConnection], not from here.
 */
fun fakeHttpEngine(json: Json): MockEngine = MockEngine { request ->
    val path = request.url.encodedPath
    val body = LocalResponder(json).bodyFor(request.method.value, path)
    respond(
        content = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), body),
        status = HttpStatusCode.OK,
        headers = headersOf("Content-Type", ContentType.Application.Json.toString()),
    )
}

/** The payloads local mode answers with, one per backend path. */
internal class LocalResponder(private val json: Json) {

    fun bodyFor(method: String, path: String): kotlinx.serialization.json.JsonObject = when {
        path == BackendPaths.GUEST -> encode(GuestCreated.serializer(), GuestCreated(LocalBackendSession.USER_ID, LocalBackendSession.SECRET))

        path == BackendPaths.ME && method == "GET" -> encode(Me.serializer(), localMe())
        path == BackendPaths.ME -> encode(Me.serializer(), localMe())

        path == BackendPaths.MY_ROOMS -> encode(MyRooms.serializer(), MyRooms(emptyList()))
        path.startsWith("/rooms/preview/") ->
            encode(RoomPreview.serializer(), RoomPreview("local-room", path.lastPathSegment(), "Local Room", null, 1, 1, true))

        path == BackendPaths.FRIENDS -> encode(Friends.serializer(), Friends(emptyList()))
        path == BackendPaths.FRIEND_REQUESTS && method == "GET" -> encode(FriendRequests.serializer(), FriendRequests(emptyList()))
        path == BackendPaths.FRIEND_REQUESTS -> encode(SendFriendRequestReply.serializer(), SendFriendRequestReply(FriendRequestOutcomeCode.NOT_FOUND))

        path == BackendPaths.BLOCKS && method == "GET" -> encode(BlockedUsers.serializer(), BlockedUsers(emptyList()))

        path == BackendPaths.ONLINE_STATS -> encode(OnlineStats.serializer(), OnlineStats(online = LOCAL_ONLINE))
        // No unfinished match, so the app never offers to rejoin a dead one.
        path == BackendPaths.ACTIVE_MATCH -> kotlinx.serialization.json.JsonObject(
            mapOf("matchId" to kotlinx.serialization.json.JsonPrimitive("")),
        )

        // Voice is not used in local mode; a placeholder token is enough.
        path.endsWith("/voice-token") -> encode(VoiceToken.serializer(), VoiceToken("wss://local", "local", 0))

        else -> kotlinx.serialization.json.JsonObject(emptyMap())
    }

    private fun localMe() = Me(
        userId = LocalBackendSession.USER_ID,
        displayName = LocalBackendSession.DISPLAY_NAME,
        xp = LocalBackendSession.XP,
        level = LocalBackendSession.LEVEL,
        coins = 0,
        gems = 0,
        username = "local",
        userCode = LocalBackendSession.USER_CODE,
        avatarId = LocalBackendSession.AVATAR_ID,
        gamesPlayed = LocalBackendSession.GAMES_PLAYED,
        gamesWon = LocalBackendSession.GAMES_WON,
    )

    private fun <T> encode(serializer: kotlinx.serialization.KSerializer<T>, value: T): kotlinx.serialization.json.JsonObject =
        json.encodeToJsonElement(serializer, value).let { it as kotlinx.serialization.json.JsonObject }

    private fun String.lastPathSegment(): String = substringAfterLast('/')

    private companion object {
        const val LOCAL_ONLINE = 1
    }
}