package com.kglabs28.yapnplay.shared.game.api

/**
 * A player action. Each game defines its own actions;
 * Ludo uses RollDice and MovePiece.
 */
sealed interface Action {
    data object RollDice : Action
    data class MovePiece(val pieceId: PieceId) : Action
}
