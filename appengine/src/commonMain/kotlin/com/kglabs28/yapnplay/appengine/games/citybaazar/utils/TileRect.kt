package com.kglabs28.yapnplay.appengine.games.citybaazar.utils

/** A tile's area on the board, in board units (x right, y down). */
data class TileRect(val x0: Float, val y0: Float, val x1: Float, val y1: Float) {
    val centreX: Float get() = (x0 + x1) / 2
    val centreY: Float get() = (y0 + y1) / 2
}
