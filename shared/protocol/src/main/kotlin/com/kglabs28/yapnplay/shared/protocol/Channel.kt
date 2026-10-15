package com.kglabs28.yapnplay.shared.protocol

/**
 * The two logical channels carried by one WebSocket connection.
 */
enum class Channel {
    /** Room membership, readiness, countdown. */
    ROOM,

    /** Game-specific match events. */
    MATCH,
}
