package com.kglabs28.yapnplay.appengine.core.network


/** Every backend path (docs/backend-api-contract.md). Clients never type a URL. */
object BackendPaths {
    const val GUEST = "/auth/guest"
    const val PLAY_GAMES = "/auth/play-games"
    const val ME = "/me"
    const val MY_ROOMS = "/rooms/mine"
    const val ROOM_INVITES = "/rooms/invites"
    const val FRIENDS = "/friends"
    const val FRIEND_REQUESTS = "/friends/requests"
    const val BLOCKS = "/blocks"
    const val DEVICES = "/devices"
    const val ONLINE_STATS = "/stats/online"
    const val CLEAR_PENALTY_WITH_AD = "/penalties/clear-with-ad"
    const val ACTIVE_MATCH = "/matches/active"
    const val WEB_SOCKET = "/ws"

    fun roomPreview(code: String) = "/rooms/preview/$code"
    fun friend(userId: String) = "$FRIENDS/$userId"
    fun inviteToRoom(roomId: String) = "/rooms/$roomId/invites"
    fun acceptRoomInvite(inviteId: String) = "$ROOM_INVITES/$inviteId/accept"
    fun declineRoomInvite(inviteId: String) = "$ROOM_INVITES/$inviteId/decline"
    fun acceptFriendRequest(requestId: String) = "$FRIEND_REQUESTS/$requestId/accept"
    fun declineFriendRequest(requestId: String) = "$FRIEND_REQUESTS/$requestId/decline"
    fun block(userId: String) = "$BLOCKS/$userId"
    fun roomVoiceToken(roomId: String) = "/rooms/$roomId/voice-token"
    fun matchVoiceToken(matchId: String) = "/matches/$matchId/voice-token"

    /** ws:// (or wss://) form of the backend address [baseUrl], for the WebSocket. */
    fun webSocketUrl(baseUrl: String): String = baseUrl
        .replaceFirst(HTTPS, WSS)
        .replaceFirst(HTTP, WS) + WEB_SOCKET

    private const val HTTPS = "https://"
    private const val WSS = "wss://"
    private const val HTTP = "http://"
    private const val WS = "ws://"
}
