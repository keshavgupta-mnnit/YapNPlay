package com.kglabs28.yapnplay.appengine.protocol.transport

/**
 * Message type strings used in the Envelope.
 * The receiver uses these to deserialize the payload correctly.
 */
object MessageType {
    // ── Room channel: client to server ────────────────────────────────────
    const val CREATE_ROOM = "CreateRoom"
    const val JOIN_ROOM = "JoinRoom"
    const val LEAVE_ROOM = "LeaveRoom"
    const val EXIT_LOBBY = "ExitLobby"
    const val SET_READY = "SetReady"
    const val START_COUNTDOWN = "StartCountdown"
    const val CANCEL_COUNTDOWN = "CancelCountdown"
    const val REQUEST_SNAPSHOT = "RequestSnapshot"
    const val SELECT_GAME = "SelectGame"
    const val SET_BOT_FILL = "SetBotFill"

    // ── Room channel: server to client ────────────────────────────────────
    const val ACK = "Ack"
    const val REJECTED = "Rejected"
    const val ROOM_SNAPSHOT = "RoomSnapshot"
    const val MATCH_STARTING = "MatchStarting"

    // ── Match channel: client to server ───────────────────────────────────
    const val ROLL_DICE = "RollDice"
    const val MOVE_PIECE = "MovePiece"

    // ── Match channel: server to client ───────────────────────────────────
    const val MATCH_SNAPSHOT = "MatchSnapshot"
    const val MATCH_EVENT = "MatchEvent"
    const val MATCH_ENDED = "MatchEnded"

    // ── Matchmaking channel: client to server ─────────────────────────────
    const val JOIN_QUEUE = "JoinQueue"
    const val LEAVE_QUEUE = "LeaveQueue"

    // ── Matchmaking channel: server to client ─────────────────────────────
    const val QUEUE_STATUS = "QueueStatus"
    const val MATCH_FOUND = "MatchFound"
    const val QUEUE_CANCELLED = "QueueCancelled"
}
