package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.kglabs28.yapnplay.appengine.games.cyberblast.utils.CyberWindow

/**
 * Where the board's camera looks. It follows the user's character until
 * they scroll (the bars or a drag on the board); the next push on the pad
 * brings it back to them. The canvas tells it the last window it drew, so a
 * drag moves by whole tiles and stops at the arena's edges.
 */
@Stable
class CyberCameraState {
    /** Where the user scrolled to (tiles); null: follow their character. */
    val panned = mutableStateOf<Pair<Float, Float>?>(null)
    /** The last window drawn, its tile size (px) and the board's size: for drags. */
    var window: CyberWindow = CyberWindow(0f, 0f, 0f, 0f)
    var tilePx: Float = 1f
    var boardCols: Int = 0
    var boardRows: Int = 0

    val canScroll: Boolean get() = window.cols < boardCols || window.rows < boardRows

    /** Back on the user's character. */
    fun follow() {
        panned.value = null
    }

    /** Moves the view by [dx], [dy] tiles, from where it is now. */
    fun scrollBy(dx: Float, dy: Float) {
        if (!canScroll) return
        val cx = window.left + window.cols / 2 + dx
        val cy = window.top + window.rows / 2 + dy
        val halfW = window.cols / 2
        val halfH = window.rows / 2
        panned.value = cx.coerceIn(halfW, boardCols - halfW) to cy.coerceIn(halfH, boardRows - halfH)
    }

    /** A finger dragging the board: the arena moves with it. */
    fun dragBoard(dxPx: Float, dyPx: Float) = scrollBy(-dxPx / tilePx, -dyPx / tilePx)

    /** A finger dragging a scroll bar's thumb: the view moves the same way, scaled to the whole arena. */
    fun dragBar(horizontal: Boolean, deltaPx: Float) {
        if (horizontal) {
            scrollBy(deltaPx / (window.cols * tilePx) * boardCols, 0f)
        } else {
            scrollBy(0f, deltaPx / (window.rows * tilePx) * boardRows)
        }
    }
}
