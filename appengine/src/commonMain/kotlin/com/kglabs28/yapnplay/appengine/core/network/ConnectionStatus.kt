package com.kglabs28.yapnplay.appengine.core.network

/** Whether the app can reach the backend right now (from the WebSocket). */
enum class ConnectionStatus {
    /** First try, or a retry, is under way. */
    CONNECTING,
    ONLINE,
    /** The last try failed (no internet or the server is down); retrying with pauses. */
    OFFLINE,
}
