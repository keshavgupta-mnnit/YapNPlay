package com.kglabs28.yapnplay.appengine.core.common

/** Where an incoming link (invite or notification) should take the user. */
sealed interface DeepLink {
    /** Invite link: show "Join this room?" for [code]. */
    data class JoinRoom(val code: String) : DeepLink

    /** Notification tap: open the lobby of a room the user is in. */
    data class OpenRoom(val roomId: String) : DeepLink

    /** A shared YapNPlay ID: send its owner a friend request. */
    data class AddFriend(val userCode: String) : DeepLink

    /** Game Room invite push tap: the Rooms tab with the pending invites to accept or decline. */
    data object OpenInvites : DeepLink

    /** Friend request push tap: the profile with the friend requests to accept or decline. */
    data object OpenFriendRequests : DeepLink
}

/**
 * Builds and reads the app links (the app registers `yapnplay://`):
 * `yapnplay://join/<code>` (a room invite), `yapnplay://friend/<code>`
 * (a shared YapNPlay ID: a friend request), `yapnplay://room/<roomId>`,
 * `yapnplay://invites` and `yapnplay://friend-requests` (notifications). `https://<INVITE_HOST>/join/<code>` is read too, for
 * when that web host is live.
 */
object DeepLinks {

    fun roomUri(roomId: String): String =
        "${AppConstants.APP_SCHEME}://$ROOM_PATH/$roomId"

    fun invitesUri(): String = "${AppConstants.APP_SCHEME}://$INVITES_PATH"

    fun friendRequestsUri(): String = "${AppConstants.APP_SCHEME}://$REQUESTS_PATH"

    /**
     * The shared room invite: an https page on the backend ([serverUrl])
     * that opens the app on `yapnplay://join/<code>`, or the store without it.
     */
    fun joinLink(serverUrl: String, code: String): String = "$serverUrl/$OPEN_PATH/$JOIN_PATH/$code"

    /** The shared YapNPlay ID: like [joinLink], opening `yapnplay://friend/<code>` (a friend request). */
    fun friendLink(serverUrl: String, userCode: String): String = "$serverUrl/$OPEN_PATH/$FRIEND_PATH/$userCode"

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
            FRIEND_PATH -> path.getOrNull(1)?.let { DeepLink.AddFriend(it.substringBefore('?')) }
            INVITES_PATH -> DeepLink.OpenInvites
            REQUESTS_PATH -> DeepLink.OpenFriendRequests
            else -> null
        }
    }

    private const val OPEN_PATH = "open"
    private const val JOIN_PATH = "join"
    private const val ROOM_PATH = "room"
    private const val FRIEND_PATH = "friend"
    private const val INVITES_PATH = "invites"
    private const val REQUESTS_PATH = "friend-requests"
}
