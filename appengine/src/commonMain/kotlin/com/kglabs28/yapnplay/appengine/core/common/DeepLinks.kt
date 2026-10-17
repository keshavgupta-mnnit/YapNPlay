package com.kglabs28.yapnplay.appengine.core.common

/** Where an incoming link (invite or notification) should take the user. */
sealed interface DeepLink {
    /** Invite link: show "Join this room?" for [code]. */
    data class JoinRoom(val code: String) : DeepLink

    /** Notification tap: open the lobby of a room the user is in. */
    data class OpenRoom(val roomId: String) : DeepLink
}

/**
 * Builds and reads the app links. Shared invites are web links, which
 * every chat app makes tappable: `https://<INVITE_HOST>/join/<code>`.
 * Android opens the app directly once the host serves assetlinks.json
 * (server side). `yapnplay://join/<code>` is read too.
 * Notifications: `yapnplay://room/<roomId>`.
 */
object DeepLinks {

    /** The link put in share messages. */
    fun inviteUrl(code: String): String =
        "https://${AppConstants.INVITE_HOST}/$JOIN_PATH/$code"

    fun roomUri(roomId: String): String =
        "${AppConstants.APP_SCHEME}://$ROOM_PATH/$roomId"

    /** null when [uri] is not one of our links. */
    fun parse(uri: String?): DeepLink? {
        if (uri.isNullOrBlank()) return null
        if (!uri.contains("://")) return null
        val withoutScheme = uri.substringAfter("://")
        val parts = withoutScheme.split('/').filter { it.isNotBlank() }
        // https://host/join/<code>  → [host, join, code]
        // yapnplay://join/<code>    → [join, code]
        // yapnplay://room/<roomId>  → [room, roomId]
        val path = if (uri.startsWith(AppConstants.APP_SCHEME + "://")) parts else parts.drop(1)
        return when (path.getOrNull(0)) {
            JOIN_PATH -> path.getOrNull(1)?.let { DeepLink.JoinRoom(it.substringBefore('?')) }
            ROOM_PATH -> path.getOrNull(1)?.let { DeepLink.OpenRoom(it.substringBefore('?')) }
            else -> null
        }
    }

    private const val JOIN_PATH = "join"
    private const val ROOM_PATH = "room"
}
