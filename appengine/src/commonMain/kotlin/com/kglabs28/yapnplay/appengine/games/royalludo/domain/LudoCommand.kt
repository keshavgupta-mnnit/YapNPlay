package com.kglabs28.yapnplay.appengine.games.royalludo.domain

/** What the player can do at the table. */
sealed interface LudoCommand {
    data object Roll : LudoCommand
    data class Move(val piece: Int) : LudoCommand
    data class SetAutoplay(val enabled: Boolean) : LudoCommand
    data class SetMic(val on: Boolean) : LudoCommand
    data object Leave : LudoCommand
}
