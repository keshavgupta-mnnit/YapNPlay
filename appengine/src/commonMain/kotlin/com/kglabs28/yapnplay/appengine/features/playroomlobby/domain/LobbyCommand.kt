package com.kglabs28.yapnplay.appengine.features.playroomlobby.domain

/**
 * Everything the user can ask the room to do from the lobby. The server
 * checks every role rule again; the app only hides what makes no sense.
 */
sealed interface LobbyCommand {
    /** Leader: pick a game, or null to go back to choosing. */
    data class SelectGame(val gameId: String?) : LobbyCommand
    data class SetHouseRule(val key: String, val enabled: Boolean) : LobbyCommand
    data class SetBotFill(val enabled: Boolean) : LobbyCommand
    data class SetReady(val ready: Boolean) : LobbyCommand
    data class SetWatching(val watching: Boolean) : LobbyCommand
    data class SetVoiceState(val micOn: Boolean, val speakerOn: Boolean) : LobbyCommand
    data object StartCountdown : LobbyCommand
    data object CancelCountdown : LobbyCommand
    data class TransferLeader(val userId: String) : LobbyCommand
    data class TransferAdmin(val userId: String) : LobbyCommand
    data class SetFriend(val userId: String, val friend: Boolean) : LobbyCommand
    /** Admin: let [userId] in from the Waiting List. */
    data class ApproveJoin(val userId: String) : LobbyCommand

    /** Admin: turn down [userId]'s request. */
    data class DeclineJoin(val userId: String) : LobbyCommand

    /** Admin: take [userId] out of the room. */
    data class RemoveMember(val userId: String) : LobbyCommand
    /** Back arrow: stop being present, stay a member. */
    data object ExitLobby : LobbyCommand
    /** Leave room: give up membership. */
    data object LeaveRoom : LobbyCommand
}
