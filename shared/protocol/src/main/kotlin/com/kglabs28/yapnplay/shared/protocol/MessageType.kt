package com.kglabs28.yapnplay.shared.protocol

/**
 * Message type strings used in the Envelope.
 * The receiver uses these to deserialize the payload correctly.
 */
object MessageType {
    // ── Room channel: client → server ────────────────────────────────────
    const val CREATE_ROOM = "CreateRoom"
    const val JOIN_ROOM = "JoinRoom"
    const val LEAVE_ROOM = "LeaveRoom"
    const val EXIT_LOBBY = "ExitLobby"
    const val SET_READY = "SetReady"
    const val START_COUNTDOWN = "StartCountdown"
    const val CANCEL_COUNTDOWN = "CancelCountdown"
    const val REQUEST_SNAPSHOT = "RequestSnapshot"

    // ── Room channel: server → client ────────────────────────────────────
    const val ACK = "Ack"
    const val REJECTED = "Rejected"
    const val ROOM_SNAPSHOT = "RoomSnapshot"
    const val MATCH_STARTING = "MatchStarting"

    // ── Match channel: client → server ───────────────────────────────────
    const val ROLL_DICE = "RollDice"
    const val MOVE_PIECE = "MovePiece"

    // ── Match channel: server → client ───────────────────────────────────
    const val MATCH_SNAPSHOT = "MatchSnapshot"
    const val MATCH_EVENT = "MatchEvent"
    const val MATCH_ENDED = "MatchEnded"
}
