package com.kglabs28.yapnplay.appengine.games.elements.pieces.mechanics

import com.kglabs28.yapnplay.appengine.games.base.play.Action

/**
 * Move a piece. Shared by piece games (Royal Ludo, Monopoly-style games).
 */
data class MovePiece(val pieceId: PieceId) : Action
