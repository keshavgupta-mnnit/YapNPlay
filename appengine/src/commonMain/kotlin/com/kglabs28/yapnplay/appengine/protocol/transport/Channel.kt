package com.kglabs28.yapnplay.appengine.protocol.transport

/**
 * The two logical channels carried by one WebSocket connection.
 */
enum class Channel {
    /** Room membership, readiness, countdown. */
    ROOM,

    /** Game-specific match events. */
    MATCH,
}
