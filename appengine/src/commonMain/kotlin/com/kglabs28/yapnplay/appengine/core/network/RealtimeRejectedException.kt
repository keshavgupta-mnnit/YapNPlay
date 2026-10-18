package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.protocol.ErrorCode

/** The backend answered a WebSocket request with `Rejected(code, message)`. */
class RealtimeRejectedException(
    val code: ErrorCode,
    override val message: String,
) : RuntimeException(message)
