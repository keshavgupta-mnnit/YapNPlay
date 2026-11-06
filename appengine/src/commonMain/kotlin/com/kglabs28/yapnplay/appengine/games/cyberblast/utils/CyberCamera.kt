package com.kglabs28.yapnplay.appengine.games.cyberblast.utils

/** The part of the board on screen, in tiles: its top left corner and its size. */
data class CyberWindow(val left: Float, val top: Float, val cols: Float, val rows: Float)

/** How the board fits its space: one tile's size (px), and how many tiles show each way. */
data class CyberView(val tile: Float, val cols: Float, val rows: Float)

/**
 * What part of the arena the screen shows. A usual board shows whole, as
 * big as its space allows. A Big Arena is drawn at a usual board's height
 * (its rows fill the space top to bottom) and as many columns as the width
 * takes; the window is centred on a point (the user's character, or where
 * they scrolled to) and stops at the arena's edges.
 */
object CyberCamera {
    const val VIEW_COLS = 15
    const val VIEW_ROWS = 11

    /** The tile size and visible tiles for a [cols] × [rows] board in [width] × [height] px (a tile of frame round it). */
    fun view(cols: Int, rows: Int, width: Float, height: Float): CyberView {
        if (cols <= VIEW_COLS && rows <= VIEW_ROWS) {
            val tile = minOf(width / (cols + FRAME_TILES), height / (rows + FRAME_TILES))
            return CyberView(tile, cols.toFloat(), rows.toFloat())
        }
        // A window onto a Big Arena keeps only a slim border round it.
        val shownRows = minOf(rows, VIEW_ROWS)
        val tile = minOf(height / (shownRows + WINDOW_BORDER * 2), width / (VIEW_COLS + WINDOW_BORDER * 2))
        val shownCols = minOf(cols.toFloat(), width / tile - WINDOW_BORDER * 2)
        return CyberView(tile, shownCols, shownRows.toFloat())
    }

    /** The window of [view] centred on [centre] (tiles), kept inside the board; null centre: the board's middle. */
    fun window(cols: Int, rows: Int, view: CyberView, centre: Pair<Float, Float>?): CyberWindow {
        val c = centre ?: (cols / 2f to rows / 2f)
        val left = (c.first - view.cols / 2).coerceIn(0f, (cols - view.cols).coerceAtLeast(0f))
        val top = (c.second - view.rows / 2).coerceIn(0f, (rows - view.rows).coerceAtLeast(0f))
        return CyberWindow(left, top, view.cols, view.rows)
    }

    /** A tile of the arena's frame on each side. */
    private const val FRAME_TILES = 2

    /** The slim border round a Big Arena's window, in tiles (each side). */
    const val WINDOW_BORDER = 0.3f
}
